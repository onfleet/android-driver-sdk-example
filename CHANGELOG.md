# Change Log
Breaking changes and additions to Onfleet SDK will be documented in this file.

## [0.12.0]

Breaking changes to API

### Added Features

- Custom fields, see https://support.onfleet.com/hc/en-us/articles/21799942217748-Custom-Fields
- Route plans, see https://support.onfleet.com/hc/en-us/articles/25492148360596-Route-Plans
- Self-assign routes, see https://support.onfleet.com/hc/en-us/articles/360041740172-Self-Assign-Tasks-Routes
- END_AT_HUB TaskType, see https://support.onfleet.com/hc/en-us/articles/34992206461716-End-Route-Return-to-Hub
- END_AT_HOME TaskType, see https://support.onfleet.com/hc/en-us/articles/34992206461716-End-Route-Return-to-Hub
- ROUTE_LOAD TaskType
- BULK_PICK_UP TaskType
- Requirement state HIDDEN
- Custom task completion requirements
- Custom completion reasons, see https://support.onfleet.com/hc/en-us/articles/9382652814228-Custom-Task-Completion-Reasons
- Age Attestation requirement, see https://support.onfleet.com/hc/en-us/articles/10373142665364-Complete-a-Task#h_01GGAK84GT77TGWRK5GHTH065X
- Order short id visibility on task object
- Completed task PII enabled/disabled settings, see https://support.onfleet.com/hc/en-us/articles/38159626547348-Remove-Personally-Identifiable-Information-PII-from-Driver-Task-History

### Added

- CompletedTask has custom field support in the public model (`getCustomFields()`)
- Custom field checklist variant added: `CustomField.CustomFieldChecklist`
- Route model added (`Route`) to support route plans
- TasksManager has new `getRoutes()` method
- TasksManager has new `selfAssignRoutes()` method
- TasksManager has new `getSelfAssignableTasks()` method (task list split)
- Task and CompletedTask now expose `TaskType` in public API
- Requirements model for task completion added (`Requirements`, `RequirementState`)
- Task completion CustomRequirements added
- Package completion reasons exposed (`PackageCompletionReason`)
- Task object has a new field isRecipientNumberActive related to the offline mode
- Task object has a new field attestationAge
- Task object has orderShortId
- Organization object has a new settings completedTaskPIIEnabled
- Organization object has a new settings warnWhenStartTaskOnDifferentRoute

### Changed

- CompletedTask attachments are now of type `CompletedTaskAttachment` (previous attachment helper methods moved accordingly)
- Task/CompletedTask signatures expanded with requirements and task-type payloads (`Requirements`, `TaskType`, `CustomRequirements`)
- TasksManager task assignment changed: `selfAssignTask()` renamed/split into `selfAssignTasks()` and `selfAssignRoutes()`
- Sync type enum removed (`SyncType`) and sync payloads now use updated sync status/data semantics
- Account deletion response enum removed (`DeleteAccountResponse`)
- Android minimum supported SDK increased from 24 to 26 (consumer apps must target `minSdk >= 26`)
- CompletedTask isPickupTask has been removed. Use type (TaskType) instead
- CompletedTask metadata type is renamed to Metadata
- Task isPickupTask has been removed. Use type (TaskType) instead
- Task recipientMetadata type is renamed to Metadata
- Task metadata type is renamed to Metadata
- DriverManager getDriver() is now nullable instead of throwing exception
- SessionManager getOrganization() is now nullable instead of throwing exception

## [0.11.1]

No changes. Only a proguard fix to avoid dependency conflicts on the obfuscated classes.

## [0.11.0]

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

## [0.10.5]

### Added

- added preventStartTaskOutOfOrder to Organization

### Changed

- enforceTaskOrder renamed to warnStartTaskOutOfOrder in Organization

### Fixed

## [0.10.3]

### Added

### Changed

### Fixed