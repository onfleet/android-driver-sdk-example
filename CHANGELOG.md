# Change Log
Breaking changes and additions to to Onfleet SDK will be documented in this file.

## [0.11.1] - 2023-09-14

No changes. Only a proguard fix to avoid dependency conflicts on the obfuscated classes.

## [0.11.0] - 2023-06-19

Breaking changes to API

### Added

- CompletedTask has successReason property that supports [custom reasons](https://support.onfleet.com/hc/en-us/articles/9382652814228)
- CompletedTasksResponseStatus is added
- Organization contains an id, completionFailureReasons and completionSuccessReasons for the custom reasons support
- TaskCompletionDetails adds successNotes to track separately failure and success notes
- TaskCompletionReason added for the custom reasons support
- SDK now uses Google Play Integrity. Please enable that for your app in Play Store: https://developer.android.com/google/play/integrity/setup#apps-on-google-play

### Changed

- CompletedTasksResponse changes from enum to an object that returns a list of tasks and status
- Task removes isSameOrgAsCreator and adds a dependencies list of ids that should be completed before the task
- TaskCompletionDetails removes failureReason and adds completionStatusReason for the custom reasons support
- TasksManager selfAssignTask function now takes a list of ids to be self assigned
- TasksManager getCompletedTasksFlow now returns the new CompletedTasksResponse and tasks are fetched during subscription to the flow
- TasksManager refreshCompletedTasks doesn't return CompletedTasksResponse and is used only to force refresh the tasks

### Fixed

## [0.10.5] - 2023-06-01

### Added

- added preventStartTaskOutOfOrder to Organization

### Changed

- enforceTaskOrder renamed to warnStartTaskOutOfOrder in Organization

### Fixed

## [0.10.3]

### Added

### Changed

### Fixed