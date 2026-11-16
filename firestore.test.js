const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function pastTimestamp() {
  return new Date(Date.now() - 2000);
}

function validProfilePayload(uid, ts = pastTimestamp()) {
  return {
    userId: uid,
    learnerName: "Mia",
    avatarId: "star",
    xp: 120,
    level: 2,
    totalStars: 5,
    soundEnabled: true,
    dailyTimerEnabled: false,
    dailyLimitMinutes: 30,
    parentPin: "1234",
    createdAt: ts,
    updatedAt: ts,
  };
}

function validGameProgressPayload(uid, gameId = "phonics", ts = pastTimestamp()) {
  return {
    userId: uid,
    gameId,
    stars: 3,
    bestScore: 100,
    playsCount: 1,
    completed: true,
    createdAt: ts,
    updatedAt: ts,
  };
}

function validStickerPayload(uid, stickerId = "first_star", ts = pastTimestamp()) {
  return {
    userId: uid,
    stickerId,
    title: "First Star!",
    category: "milestone",
    createdAt: ts,
    updatedAt: ts,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read or write user profiles or subcollections", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("gameProgress").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("stickers").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).set(validProfilePayload(ALICE_UID)));
});

test("Authenticated owner: can create and update their own profile with valid actions", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const profileRef = aliceDb.collection("users").doc(ALICE_UID);

  await assertSucceeds(profileRef.set(validProfilePayload(ALICE_UID)));
  await assertSucceeds(profileRef.get());

  // Action: Update learner profile
  await assertSucceeds(
    profileRef.update({
      learnerName: "Leo",
      avatarId: "cloud",
      updatedAt: pastTimestamp(),
    })
  );

  // Action: Update XP & stars
  await assertSucceeds(
    profileRef.update({
      xp: 220,
      level: 3,
      totalStars: 8,
      updatedAt: pastTimestamp(),
    })
  );

  // Action: Update parent settings
  await assertSucceeds(
    profileRef.update({
      soundEnabled: false,
      dailyTimerEnabled: true,
      dailyLimitMinutes: 45,
      parentPin: "4321",
      updatedAt: pastTimestamp(),
    })
  );
});

test("Shadow update & invalid PIN / avatar: rejected by rules", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const profileRef = aliceDb.collection("users").doc(ALICE_UID);
  await assertSucceeds(profileRef.set(validProfilePayload(ALICE_UID)));

  // Ghost field injection
  await assertFails(
    profileRef.update({
      learnerName: "Leo",
      isAdmin: true,
      updatedAt: pastTimestamp(),
    })
  );

  // Invalid PIN format
  await assertFails(
    profileRef.update({
      parentPin: "abcd",
      updatedAt: pastTimestamp(),
    })
  );

  // Invalid avatarId
  await assertFails(
    profileRef.update({
      avatarId: "dragon",
      updatedAt: pastTimestamp(),
    })
  );
});

test("Cross-user isolation: Bob cannot read or write Alice's profile, gameProgress, or stickers", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set(validProfilePayload(ALICE_UID)));
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("gameProgress").doc("phonics").set(validGameProgressPayload(ALICE_UID, "phonics"))
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("stickers").doc("first_star").set(validStickerPayload(ALICE_UID, "first_star"))
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("gameProgress").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("stickers").get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("gameProgress").doc("phonics").set(validGameProgressPayload(BOB_UID, "phonics"))
  );
});

test("Authenticated owner: can write and list gameProgress and stickers within schema bounds", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const gameRef = aliceDb.collection("users").doc(ALICE_UID).collection("gameProgress").doc("counting");
  const stickerRef = aliceDb.collection("users").doc(ALICE_UID).collection("stickers").doc("counting_ace");

  await assertSucceeds(gameRef.set(validGameProgressPayload(ALICE_UID, "counting")));
  await assertSucceeds(gameRef.update({ stars: 3, bestScore: 150, playsCount: 2, completed: true, updatedAt: pastTimestamp() }));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("gameProgress").get());

  // Stars out of range (>3) fails
  await assertFails(gameRef.update({ stars: 5, updatedAt: pastTimestamp() }));

  await assertSucceeds(stickerRef.set(validStickerPayload(ALICE_UID, "counting_ace")));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("stickers").get());
});
