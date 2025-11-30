package com.jakir.filehelper;

import static com.jakir.filehelper.FilesExtensionHelper.getExtension;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jakir.pref.Pref;

import java.io.File;
import java.util.Comparator;

//
// Created by JAKIR HOSSAIN on 8/29/2025.
//
public class FileShortHelper {

    public static void showSortDialog(Context context) {
        // Inflate custom dialog layout
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_sort, null);

        RadioGroup rgType = view.findViewById(R.id.rgSortType);
        RadioGroup rgOrder = view.findViewById(R.id.rgSortOrder);
        CheckBox cbFoldersFirst = view.findViewById(R.id.cbFoldersFirst);

        // Load saved prefs
        int typePref = Pref.getInteger(Key.sortType, context);
        int orderPref = Pref.getInteger(Key.sortOrder, context);
        boolean foldersFirst = Pref.getBoolean(Key.foldersFirst, context,true);

        // Preselect current values
        rgType.check(getTypeRadioId(typePref));
        rgOrder.check(orderPref == FileShortHelper.SortOrder.ASCENDING.ordinal() ? R.id.rbAsc : R.id.rbDesc);
        cbFoldersFirst.setChecked(foldersFirst);

        // Create and show dialog
        AlertDialog dialog = new MaterialAlertDialogBuilder(context).setTitle("Sort by").setView(view).setPositiveButton("Apply", (d, which) -> {
            // Save type
            int typeId = rgType.getCheckedRadioButtonId();
            FileShortHelper.SortType type = getTypeFromRadioId(typeId);
            Pref.setInteger(Key.sortType,type.ordinal(),  context);

            // Save order
            int orderId = rgOrder.getCheckedRadioButtonId();
            FileShortHelper.SortOrder order = orderId == R.id.rbAsc ? FileShortHelper.SortOrder.ASCENDING : FileShortHelper.SortOrder.DESCENDING;
            Pref.setInteger( Key.sortOrder,order.ordinal(), context);

            // Save foldersFirst
            Pref.setBoolean( Key.foldersFirst,cbFoldersFirst.isChecked(), context);

            ((Activity) context).recreate();
        }).setNegativeButton("Cancel", null).setCancelable(false).create()
                ;
        dialog.show();
    }

    // Convert saved type ordinal to RadioButton ID
    private static int getTypeRadioId(int typeOrdinal) {
        SortType type = SortType.values()[typeOrdinal];
        if (type == SortType.NAME) return R.id.rbName;
        else if (type == SortType.SIZE) return R.id.rbSize;
        else if (type == SortType.DATE) return R.id.rbDate;
        else if (type == SortType.TYPE) return R.id.rbType;
        else return R.id.rbName;
    }

    // Convert selected RadioButton ID to SortType
    private static SortType getTypeFromRadioId(int radioId) {
        if (radioId == R.id.rbName) return SortType.NAME;
        else if (radioId == R.id.rbSize) return SortType.SIZE;
        else if (radioId == R.id.rbDate) return SortType.DATE;
        else if (radioId == R.id.rbType) return SortType.TYPE;
        else return SortType.NAME;
    }

    public static Comparator<File> getComparator(SortType type, SortOrder order, boolean foldersFirst) {
        Comparator<File> comparator;

        switch (type) {
            case NAME:
                comparator = Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER);
                break;
            case TYPE:
                comparator = (f1, f2) -> {
                    String ext1 = getExtension(f1);
                    String ext2 = getExtension(f2);
                    int cmp = ext1.compareToIgnoreCase(ext2);
                    return cmp != 0 ? cmp : f1.getName().compareToIgnoreCase(f2.getName());
                };
                break;
            case DATE:
                comparator = Comparator.comparingLong(File::lastModified);
                break;
            case SIZE:
                comparator = Comparator.comparingLong(File::length);
                break;
            default:
                comparator = Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER);
        }

        // Reverse if DESCENDING
        if (order == SortOrder.DESCENDING) {
            comparator = comparator.reversed();
        }

        // Apply foldersFirst if true
        if (foldersFirst) {
            comparator = foldersFirstComparator().thenComparing(comparator);
        }

        return comparator;
    }

    // Folders first comparator
    private static Comparator<File> foldersFirstComparator() {
        return (f1, f2) -> {
            if (f1.isDirectory() && !f2.isDirectory()) return -1;
            if (!f1.isDirectory() && f2.isDirectory()) return 1;
            return 0;
        };
    }

    public enum SortType {
        TYPE, NAME, SIZE, DATE
    }

    public enum SortOrder {
        ASCENDING, DESCENDING
    }
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Usages
//File[] filesArray = ...;

// Name A-Z, folders first
//        Arrays.sort(filesArray, FileUtility.getComparator(SortType.NAME, SortOrder.ASCENDING, true));
//        Arrays.sort(filesArray, FileUtility.getComparator(SortType.NAME, SortOrder.DESCENDING, true));

// Name A-Z, folders last
//        Arrays.sort(filesArray, FileUtility.getComparator(SortType.NAME, SortOrder.ASCENDING, false));
//        Arrays.sort(filesArray, FileUtility.getComparator(SortType.NAME, SortOrder.DESCENDING, false));


// Size big-small, folders first
//        Arrays.sort(filesArray, FileUtility.getComparator(SortType.SIZE, SortOrder.ASCENDING, true));
//        Arrays.sort(filesArray, FileUtility.getComparator(SortType.SIZE, SortOrder.DESCENDING, true));

// Size big-small, folders last
//        Arrays.sort(filesArray, FileUtility.getComparator(SortType.SIZE, SortOrder.ASCENDING, false));
//        Arrays.sort(filesArray, FileUtility.getComparator(SortType.SIZE, SortOrder.DESCENDING, false));


// Date old-new, folders first
//        Arrays.sort(filesArray, FileUtility.getComparator(SortType.DATE, SortOrder.ASCENDING, true));
//        Arrays.sort(filesArray, FileUtility.getComparator(SortType.DATE, SortOrder.DESCENDING, true));

// Date old-new, folders last
//        Arrays.sort(filesArray, FileUtility.getComparator(SortType.DATE, SortOrder.ASCENDING, false));
//        Arrays.sort(filesArray, FileUtility.getComparator(SortType.DATE, SortOrder.DESCENDING, false));


// Type alphabetical, folders first
//        Arrays.sort(filesArray, FileUtility.getComparator(SortType.TYPE, SortOrder.ASCENDING, true));
//        Arrays.sort(filesArray, FileUtility.getComparator(SortType.TYPE, SortOrder.DESCENDING, true));

// Type alphabetical, folders last
//        Arrays.sort(filesArray, FileUtility.getComparator(SortType.TYPE, SortOrder.ASCENDING, false));
//        Arrays.sort(filesArray, FileUtility.getComparator(SortType.TYPE, SortOrder.DESCENDING, false));
