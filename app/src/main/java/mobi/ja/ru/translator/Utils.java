package mobi.ja.ru.translator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Класс с функциями, которые могут понадобиться не в одном месте
 * Created by Serg on 21.04.2017.
 */
public class Utils {
    /**
     * Выводим MessageBox
     * @param title - заголовок
     * @param message - сообщение
     * @param activity - Activity, на базе которой будет диалог
     * @param afterPush - колбэк после закрытия диалога
     */
    public static void MessageDialog(final String title, final String message,  final Activity activity,
                                     final Runnable afterPush) {
        activity.runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                       AlertDialog dialog = builder.setTitle(title)
                                               .setMessage(message)
                                               .setIcon(R.mipmap.ic_launcher)
                                               .setCancelable(false)
                                               .setNegativeButton("OK",
                                                       new DialogInterface.OnClickListener() {
                                                           public void onClick(DialogInterface dialog, int id) {
                                                               dialog.cancel();
                                                               if(afterPush != null)
                                                                    afterPush.run();
                                                           }
                                                       }).create();
                                       dialog.show();
                                   }
                               });
    }

    /**
     * Выводим  MessageBox c кнопками Ok, Cancel
     * @param title - заголовок
     * @param message - сообщение
     * @param activity - Activity, на базе которой будет диалог
     *
     */
    public static void OkCancelDialog(final String title, final String message,  final Activity activity,
                                     final Runnable okCallback) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                AlertDialog dialog = builder.setTitle(title)
                        .setMessage(message)
                        .setIcon(R.mipmap.ic_launcher)
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        if (okCallback != null)
                                            okCallback.run();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .create();
                dialog.show();
            }
        });
    }

    /**
     * конвертация InputStream в строку
     * @param is - поток
     * @return - строка из потока
     */
    public static String convertStreamToString(java.io.InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result != -1) {
            buf.write((byte) result);
            result = bis.read();
        }
        return buf.toString();
    }

    public static String trimPhrase(String phrase) {
        phrase = phrase.replaceAll(" +", " ");
        return phrase.trim();
    }

    /**
     * Runnable, заверщающий программу
     */
    public static class ExitRunnable implements Runnable {
        @Override
        public void run() {
            System.exit(0);
        }
    }
    public static ExitRunnable exitRunnable = new ExitRunnable();
}
