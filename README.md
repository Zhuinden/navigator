# Navigator [BETA]

A wrapper around [**simple-stack**](https://github.com/Zhuinden/simple-stack) to simplify navigation.

## BETA

Need to add:

- multi-stack support (again)
- child stacks + proper back navigation for them
- service tree integration

Need to decide:

- `onPause()`/`onResume()` and `onStop()`/`onStart()` callbacks to view controller?
- should I even *try* to add `onRequestPermission` and stuff? Conductor has it but it's magic

## Usage

- **Activity**

``` java
public class MainActivity
        extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @BindView(R.id.root)
    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Navigator.install(this, root, HistoryBuilder.single(FirstKey.create()));
    }

    @Override
    public void onBackPressed() {
        if(!Navigator.onBackPressed(this)) {
            super.onBackPressed();
        }
    }
}
```

- **ViewController**

``` java
public class FirstController
        extends ViewController
        implements Bundleable {
    public FirstController(StateKey stateKey) {
        super(stateKey);
    }

    @OnClick(R.id.first_button)
    public void firstButtonClick(View view) {
        Navigator.getBackstack(view.getContext()).goTo(SecondKey.create());
    }

    Unbinder unbinder;

    @Override
    protected void onViewCreated(View view) {
        // this is called after inflation
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    protected void onViewRestored(View view) {
        // this is called after view state (and controller state) is restored
    }

    @Override
    protected void onViewDestroyed(View view) {
        // this is called when view hierarchy is destroyed, or controller is replaced
        if(unbinder != null) {
            unbinder.unbind();
        }
    }

    /* toBundle(), fromBundle() */
}
```

- **StateKey**

``` java
@AutoValue
public abstract class FirstKey
        extends StateKey
        implements Parcelable {
    public static FirstKey create() {
        return new AutoValue_FirstKey();
    }

    @Override
    public int layout() {
        return R.layout.path_first;
    }

    @Override
    public ViewController provideViewController(Object... args) {
        return new FirstController(this);
    }

    @Override
    public ViewChangeHandler getViewChangeHandler() {
        return new SegueViewChangeHandler();
    }
}
```

## Using Navigator

In order to use Navigator, you need to add jitpack to your project root gradle:

    buildscript {
        repositories {
            // ...
            maven { url "https://jitpack.io" }
        }
        // ...
    }
    allprojects {
        repositories {
            // ...
            maven { url "https://jitpack.io" }
        }
        // ...
    }


and add the compile dependency to your module level gradle.

    compile 'com.github.Zhuinden:navigator:0.1.4'

## License

    Copyright 2017 Gabor Varadi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.