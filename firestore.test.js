const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const CLIVE_UID = "clive_123";
const MARK_UID = "mark_456";
const STRANGER_UID = "stranger_999";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

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

test("Unauthenticated user: cannot read users, games, or picks", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").get());
  await assertFails(unauthDb.collection("games").get());
  await assertFails(unauthDb.collection("picks").get());
});

test("Unauthorized stranger: cannot read or write private league games or picks", async () => {
  const strangerDb = testEnv.authenticatedContext(STRANGER_UID, { email: "stranger@gmail.com" }).firestore();
  await assertFails(strangerDb.collection("games").get());
  await assertFails(strangerDb.collection("picks").get());
  await assertFails(
    strangerDb.collection("games").doc("game_hack").set({
      weekNumber: 10,
      awayTeamShort: "HCK",
      homeTeamShort: "HCK",
      homeSpread: 0,
      isFinal: false,
    })
  );
});

test("Clive (Commissioner): can write whitelist, games, and picks", async () => {
  const cliveDb = testEnv.authenticatedContext(CLIVE_UID, { email: "clivecleary@gmail.com" }).firestore();
  
  // Clive can add Mark to whitelist
  await assertSucceeds(
    cliveDb.collection("whitelist").doc(MARK_UID).set({
      userId: MARK_UID,
      email: "mark@example.com",
      role: "player",
    })
  );

  await assertSucceeds(
    cliveDb.collection("games").doc("game_1").set({
      weekNumber: 10,
      awayTeamShort: "CIN",
      homeTeamShort: "BAL",
      homeSpread: -6.0,
      sportsbookProvider: "DraftKings",
      isFinal: false,
    })
  );

  await assertSucceeds(
    cliveDb.collection("picks").doc("pick_1").set({
      gameId: "game_1",
      playerId: "clive",
      weekNumber: 10,
      pickedTeamShort: "BAL",
      userId: CLIVE_UID,
    })
  );
});

test("Whitelisted Rival (Mark): can read and write once authorized", async () => {
  // First, Clive adds Mark's UID to whitelist
  await testEnv.withSecurityRulesDisabled(async (adminContext) => {
    await adminContext.firestore().collection("whitelist").doc(MARK_UID).set({
      userId: MARK_UID,
      role: "player",
    });
  });

  const markDb = testEnv.authenticatedContext(MARK_UID, { email: "mark@example.com" }).firestore();
  
  await assertSucceeds(
    markDb.collection("users").doc(MARK_UID).set({
      userId: MARK_UID,
      displayName: "Mark",
      email: "mark@example.com",
    })
  );

  await assertSucceeds(markDb.collection("games").get());
  await assertSucceeds(markDb.collection("picks").get());
});
