package x.being.lib_databinding_tool;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class DataBindEntity extends BaseObservable {
    @Bindable
    private String hint;
    @Bindable
    private String text;

    public DataBindEntity(String hint, String text) {
        this.hint = hint;
        this.text = text;
    }

    public DataBindEntity(String hint) {
        this.hint = hint;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
        notifyPropertyChanged(BR.hint);

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }
}
