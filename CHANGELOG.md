# Change log

Navigator 0.X.X (2017-03-XX)
---------------------------------
- Added `LayoutInflationStrategy` to allow asynchronous inflation of views

Navigator 0.1.6 (2017-03-20)
---------------------------------
- ViewController is now Bundleable by default, its methods are final
- add `onSaveControllerState()` and `onRestoreControllerState()` methods
- rename `onViewRestored()` to `onStateRestored()`
- rename `preSaveViewState()` to `preSaveState()`

Navigator 0.1.5 (2017-03-17)
---------------------------------
- Removed `service-tree` compile dependency, it's not used yet after all.

Navigator 0.1.4 (2017-03-17)
---------------------------------
- Breaking change: `provideViewController(Object...)` is now `createViewController()`

Navigator 0.1.3 (2017-03-14)
---------------------------------
- Added `ViewController.onActivityPaused()`, `ViewController.onActivityResumed()`, `ViewController.onActivityStopped()`, `ViewController.onActivityStarted()`

Navigator 0.1.2 (2017-03-13)
---------------------------------
- Added `Installer.setDeferredInitialization()` and `Navigator.executeDeferredInitialization()` to allow accessing `Backstack` before initial state change

Navigator 0.1.1 (2017-03-13)
---------------------------------
- Renamed `getAnimationHandler()` in `StateKey` to `getViewChangeHandler()`

Navigator 0.1.0 (2017-03-12)
---------------------------------
- Added initial fragment-based integration support + ViewControllers + StateKey
- change handlers (transition works as per example, but also provides segue animator)