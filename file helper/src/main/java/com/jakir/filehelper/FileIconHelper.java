package com.jakir.filehelper;

import android.content.Context;
import android.graphics.drawable.Drawable;

//
// Created by JAKIR HOSSAIN on 11/6/2025.
//
public class FileIconHelper {
    public static Drawable getFileIcon(String fileName, Context context) {
        fileName = fileName.toLowerCase();

        try {
            if (fileName.endsWith(".aac")) return context.getDrawable(R.drawable.file_ic_aac_6);
            else if (fileName.endsWith(".ai")) return context.getDrawable(R.drawable.file_ic_ai);
            else if (fileName.endsWith(".apk")) return context.getDrawable(R.drawable.file_ic_apk);
            else if (fileName.endsWith(".avi")) return context.getDrawable(R.drawable.file_ic_avi);
            else if (fileName.endsWith(".crd")) return context.getDrawable(R.drawable.file_ic_crd);
            else if (fileName.endsWith(".css"))
                return context.getDrawable(R.drawable.file_ic_css_28_256);
            else if (fileName.endsWith(".csv")) return context.getDrawable(R.drawable.file_ic_csv);
            else if (fileName.endsWith(".db")) return context.getDrawable(R.drawable.file_ic_db);
            else if (fileName.endsWith(".dll")) return context.getDrawable(R.drawable.file_ic_dll);
            else if (fileName.endsWith(".doc")) return context.getDrawable(R.drawable.file_ic_doc);
            else if (fileName.endsWith(".docx"))
                return context.getDrawable(R.drawable.file_ic_docx);
            else if (fileName.endsWith(".dwg")) return context.getDrawable(R.drawable.file_ic_dwg);
            else if (fileName.endsWith(".eps")) return context.getDrawable(R.drawable.file_ic_eps);
            else if (fileName.endsWith(".exe")) return context.getDrawable(R.drawable.file_ic_exe);
            else if (fileName.endsWith(".flv")) return context.getDrawable(R.drawable.file_ic_flv);
            else if (fileName.endsWith(".gif")) return context.getDrawable(R.drawable.file_ic_giff);
            else if (fileName.endsWith(".html"))
                return context.getDrawable(R.drawable.file_ic_html);
            else if (fileName.endsWith(".iso")) return context.getDrawable(R.drawable.file_ic_iso);
            else if (fileName.endsWith(".jar"))
                return context.getDrawable(R.drawable.file_ic_jar_11_256);
            else if (fileName.endsWith(".java"))
                return context.getDrawable(R.drawable.file_ic_java);
            else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
                return context.getDrawable(R.drawable.file_ic_jpg);
            else if (fileName.endsWith(".js"))
                return context.getDrawable(R.drawable.file_ic_js_9_256);
            else if (fileName.endsWith(".json"))
                return context.getDrawable(R.drawable.file_ic_json_3_256);
            else if (fileName.endsWith(".mov")) return context.getDrawable(R.drawable.file_ic_mov);
            else if (fileName.endsWith(".mp3")) return context.getDrawable(R.drawable.file_ic_mp3);
            else if (fileName.endsWith(".mp4")) return context.getDrawable(R.drawable.file_ic_mp4);
            else if (fileName.endsWith(".mpeg"))
                return context.getDrawable(R.drawable.file_ic_mpeg);
            else if (fileName.endsWith(".pdf")) return context.getDrawable(R.drawable.file_ic_pdf);
            else if (fileName.endsWith(".php"))
                return context.getDrawable(R.drawable.file_ic_php_13_256);
            else if (fileName.endsWith(".png")) return context.getDrawable(R.drawable.file_ic_png);
            else if (fileName.endsWith(".ppt")) return context.getDrawable(R.drawable.file_ic_ppt);
            else if (fileName.endsWith(".ps")) return context.getDrawable(R.drawable.file_ic_ps);
            else if (fileName.endsWith(".psd")) return context.getDrawable(R.drawable.file_ic_psd);
            else if (fileName.endsWith(".pub")) return context.getDrawable(R.drawable.file_ic_pub);
            else if (fileName.endsWith(".rar")) return context.getDrawable(R.drawable.file_ic_rar);
            else if (fileName.endsWith(".raw")) return context.getDrawable(R.drawable.file_ic_raw);
            else if (fileName.endsWith(".rss")) return context.getDrawable(R.drawable.file_ic_rss);
            else if (fileName.endsWith(".sql"))
                return context.getDrawable(R.drawable.file_ic_sql_8_256);
            else if (fileName.endsWith(".svg")) {
                return context.getDrawable(R.drawable.file_ic_svg);
            } else if (fileName.endsWith(".tiff") || fileName.endsWith(".tif"))
                return context.getDrawable(R.drawable.file_ic_tiff);
            else if (fileName.endsWith(".txt")) return context.getDrawable(R.drawable.file_ic_txt);
            else if (fileName.endsWith(".wav")) return context.getDrawable(R.drawable.file_ic_wav);
            else if (fileName.endsWith(".wma")) return context.getDrawable(R.drawable.file_ic_wma);
            else if (fileName.endsWith(".xml")) return context.getDrawable(R.drawable.file_ic_xml);
            else if (fileName.endsWith(".xsl")) return context.getDrawable(R.drawable.file_ic_xsl);
            else if (fileName.endsWith(".zip")) return context.getDrawable(R.drawable.file_ic_zip);
            else return context.getDrawable(R.drawable.file_ic_unknown_file); // default icon
        } catch (Exception e) {
            e.printStackTrace();
            return context.getDrawable(R.drawable.file_ic_unknown_file);
        }
    }

}
