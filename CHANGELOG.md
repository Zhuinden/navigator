# Change log

Navigator 0.3.1 (2017-03-27)
---------------------------------
- Added `ViewChangeCompletionListener` to `DefaultStateChanger` in case it's important to know when the view change has been completed.
- Ported over `simple-stack-example-mvp` and `simple-stack-example-nestedstack` to use Navigator

Navigator 0.3.0 (2017-03-27)
---------------------------------
- Breaking change: `StateKey` is now an interface, `getViewChangeHandler()` => `viewChangeHandler()`
- Breaking change: `Navigator` sets up a `DefaultStateChanger` (which is the old `InternalStateChanger` which is now public
If Navigator is configured with a different state changer, then that state changer is used entirely.
Navigator is primarily used as a means of replacing BackstackDelegate, but configurable to avoid its default behaviors.
To obtain the behavior of previous InternalStateChanger, then `new DefaultStateChanger(context, container, externalStateChanger)` should be set.
- Add: Javadocs.
- Add: `DefaultStateChanger(Context, ViewGroup)` can be used with `ViewChangeHandler` to manage the navigation between views.

Navigator 0.2.2 (2017-03-26)
---------------------------------
- `Navigator.install()` returns `Backstack` (which functions as a router)
- `Navigator.persistState()` => `Navigator.persistViewToState()`, `Navigator.restoreState()` => `Navigator.restoreViewFromState()`

Navigator 0.2.1 (2017-03-25)
---------------------------------
- Update simple-stack to 1.4.3.

Navigator 0.2.0 (2017-03-25)
---------------------------------
- Breaking change: Killed ViewControllers entirely, Navigator's internal state changer relies on custom viewgroups instead.

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