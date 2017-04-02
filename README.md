# Navigator

A wrapper around [**simple-stack**](https://github.com/Zhuinden/simple-stack) to simplify navigation.

It uses retained fragment as lifecycle listener instead of the `BackstackDelegate`, therefore Min SDK 11+ is required.

Also provides a `DefaultStateChanger` which can be created with `create()`, or configured with `configure()`.

If `DefaultStateChanger` is used, then your keys must implement the `StateKey` interface.

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
        // additional configuration possible with `Navigator.configure()...install()`
    }

    @Override
    public void onBackPressed() {
        if(!Navigator.onBackPressed(this)) {
            super.onBackPressed();
        }
    }
}
```

- **StateKey**

``` java
@AutoValue
public abstract class FirstKey
        implements StateKey, Parcelable {
    public static FirstKey create() {
        return new AutoValue_FirstKey();
    }

    @Override
    public int layout() {
        return R.layout.path_first;
    }

    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new SegueViewChangeHandler();
    }
}
```

- **Layout XML**

``` xml
<?xml version="1.0" encoding="utf-8"?>
<com.zhuinden.navigatorexample.FirstView xmlns:android="http://schemas.android.com/apk/res/android"
              android:layout_width="match_parent"
              android:layout_height="match_parent"
              android:gravity="center"
              android:orientation="vertical">

    <EditText
        android:id="@+id/first_edittext"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:hint="Enter text here"/>

    <Button
        android:id="@+id/first_button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Go to second!"/>

</com.zhuinden.navigatorexample.FirstView>
```

- **Custom Viewgroup**

``` java
public class FirstView
        extends LinearLayout
        implements Bundleable {

    public FirstView(Context context) {
        super(context);
        init(context);
    }

    public FirstView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FirstView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public FirstView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        if(!isInEditMode()) {
            // ...
        }
    }

    @OnClick(R.id.first_button)
    public void firstButtonClick(View view) {
        Navigator.getBackstack(view.getContext()).goTo(SecondKey.create());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @NonNull
    @Override
    public StateBundle toBundle() {
        StateBundle bundle = new StateBundle();
        bundle.putString("HELLO", "WORLD");
        return bundle;
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            Log.i("FIRST", bundle.getString("HELLO"));
        }
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

    compile 'com.github.Zhuinden:navigator:1.0.0'

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
