package com.zhuinden.navigatornestedstack.presentation.paths.main.mail;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigatornestedstack.R;
import com.zhuinden.navigatornestedstack.application.Key;
import com.zhuinden.navigatornestedstack.presentation.paths.main.MainView;

/**
 * Created by Zhuinden on 2017.02.19..
 */
@AutoValue
public abstract class MailKey
        extends Key {
    @Override
    public int layout() {
        return R.layout.path_mail;
    }

    public static MailKey create() {
        return new AutoValue_MailKey();
    }

    @Override
    public String stackIdentifier() {
        return MainView.StackType.MAIL.name();
    }
}
