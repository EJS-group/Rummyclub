# Implementation Plan - Test Cases for Today's Modifications

This plan outlines the creation of automated test cases to verify the key logic and UI changes implemented today.

## Proposed Changes

### Tests

#### [NEW] [RummyUiLogicTest.kt](file:///G:/app/New folder/A/rummy-club (4)/app/src/test/java/com/example/RummyUiLogicTest.kt)
- Use **Robolectric** and **Compose Test Rule**.
- **Test Case 1**: Verify "DECLARE" button visibility.
    - Assert hidden when `isHumanTurn` is false.
    - Assert hidden when `isHumanTurn` is true but `hasDrawnCardThisTurn` is false.
    - Assert visible when `isHumanTurn` is true AND `hasDrawnCardThisTurn` is true.
- **Test Case 2**: Verify `PlayerSeatView` Simple Mode.
    - Assert that chips and status details are not displayed when `isSimpleMode = true`.

#### [NEW] [RummyLobbyTest.kt](file:///G:/app/New folder/A/rummy-club (4)/app/src/test/java/com/example/RummyLobbyTest.kt)
- **Test Case 1**: Verify Flexible Grid Logic.
    - Test the `chunked(4)` logic to ensure players are correctly partitioned into rows of 4 for the flexible lobby layout.

#### [NEW] [RummyEvaluatorTest.kt](file:///G:/app/New folder/A/rummy-club (4)/app/src/test/java/com/example/RummyEvaluatorTest.kt)
- **Test Case 1**: Verify Pure Sequence detection.
- **Test Case 2**: Verify Impure Sequence detection.
- **Test Case 3**: Verify Set detection.
- **Test Case 4**: Verify full hand declaration validation (the core logic used by the "Declare" button).

## Verification Plan

### Automated Tests
- Run the newly created tests using Gradle:
    - `./gradlew :app:testDebugUnitTest`

### Manual Verification
- None required as these are automated tests.
