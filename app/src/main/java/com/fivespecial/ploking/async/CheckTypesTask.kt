/*
 * Created by Lee Oh Hyoung on 2019-10-09 .. 
 */
package com.fivespecial.ploking.async

import android.app.Activity
import android.app.ProgressDialog
import android.os.AsyncTask

class CheckTypesTask(activity: Activity) : AsyncTask<Void, Void, Void>() {

    private var asyncDialog: ProgressDialog = ProgressDialog(activity)

    override fun onPreExecute() {

        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        asyncDialog.setMessage("로딩중입니다..")

        // show dialog
        asyncDialog.show()
        super.onPreExecute()
    }

    override fun doInBackground(vararg args: Void): Void? {
        try {
            for (i in 0..4) {

                // TODO Why Sleep Thread for 2 seconds? by Lee Oh Hyoung on 2019.10.11
                //asyncDialog.setProgress(i * 30);
                Thread.sleep(500)
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        asyncDialog.dismiss()
    }
}