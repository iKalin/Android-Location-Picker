package com.example.ultron.androidlocationpicker;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchHistoryManager {
    private static final String PREFERENCE_NAME_SEARCH_HISTORY = "search-history-preference";
    private static final String sAutocompleteItems = "autocomplete-items";
    private final SharedPreferences mSharedPreferences;

    public SearchHistoryManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME_SEARCH_HISTORY, Context.MODE_PRIVATE);
    }

    public List<AutocompleteItem> searchHistory() {
        Set<String> history = mSharedPreferences.getStringSet(sAutocompleteItems, new HashSet<String>());

        ArrayList<AutocompleteItem> items = new ArrayList<>();
        for (String encoded : history) {
            AutocompleteItem decoded = decodeItem(encoded);
            if (decoded != null) {
                items.add(decoded);
            }
        }

        return items;
    }

    public void addToHistory(AutocompleteItem autocompleteItem) {
        Set<String> history = mSharedPreferences.getStringSet(sAutocompleteItems, new HashSet<String>());
        HashSet<String> newHistory = new HashSet<>(history);

        String encoded = encodeItem(autocompleteItem);
        newHistory.add(encoded);

        mSharedPreferences.edit().putStringSet(sAutocompleteItems, newHistory).apply();
    }

    private final String delimeter = "/////////";

    private String encodeItem(AutocompleteItem autocompleteItem) {
        return autocompleteItem.getName() + delimeter + autocompleteItem.getPlaceId();
    }

    @Nullable
    private AutocompleteItem decodeItem(String encoded) {
        String parts[] = encoded.split(delimeter);
        if (parts.length == 2) {
            return new AutocompleteItem(parts[0], parts[1]);
        } else {
            return null;
        }
    }
}
