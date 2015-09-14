package com.example.ultron.androidlocationpicker;

public class AutocompleteItem {
    private final String mName;
    private final String mPlaceId;

    public AutocompleteItem(String name, String placeId) {
        mName = name;
        mPlaceId = placeId;
    }

    public String getName() {
        return mName;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    @Override
    public String toString() {
        return mName;
    }
}
