package com.kmong.cyber.ad_mms_poster_user.SendMMS3;

import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class APNHelper {

	public class APN {
	    public String MMSCenterUrl = "";
	    public String MMSPort = "";
	    public String MMSProxy = "";
	}
 
	public APNHelper(final Context context) {
	    this.context = context;
	}

	public List<APN> getMMSApns() {
        //skt apn
		final List<APN> results = new ArrayList<APN>();

		final APN apn = new APN();
        /*
        skt3g
        apn: web.sktelecom.com
        mmsc : http://omms.nate.com:9082/oma_mms
        mms proxy : smart.nate.com
        mms port : 9093
        mcc : 450
        mnc : 05

        skt lte

        apn: lte.sktelecom.com
        mmsc : http://omms.nate.com:9082/oma_mms
        mms proxy : lteoma.nate.com
        mms port : 9093
        mcc : 450
        mnc : 05

        kt 3g
        apn alwayson.ktfwing.com
        mmsc : http://mmsc.ktfwing.com:9082
        mms port :9093
        mcc : 450
        mnc :08

        kt lte
        apn lte.ktfwing.com
        mmsc : http://mmsc.ktfwing.com:9082
        mms port :9093
        mcc : 450
        mnc :08


        lg u+ lte
        apn : internet.lguplus.co.kr
        mmsc : http://omammsc.uplus.co.kr:9084
        mms port :9084
        mcc : 450
        mnc : 06
         */
		apn.MMSCenterUrl = "http://omms.nate.com:9082/oma_mms";

        apn.MMSProxy = "lteoma.nate.com";;

		apn.MMSPort = "9093";

		results.add(apn);

		return results;
	}

	private Context context;
}

final class Carriers implements BaseColumns {
    /**
     * The content:// style URL for this table
     */
    public static final Uri CONTENT_URI =
        Uri.parse("content://telephony/carriers");

    /**
     * The default sort order for this table
     */
    public static final String DEFAULT_SORT_ORDER = "name ASC";

    public static final String NAME = "name";

    public static final String APN = "apn";

    public static final String PROXY = "proxy";

    public static final String PORT = "port";

    public static final String MMSPROXY = "mmsproxy";

    public static final String MMSPORT = "mmsport";

    public static final String SERVER = "server";

    public static final String USER = "user";

    public static final String PASSWORD = "password";

    public static final String MMSC = "mmsc";

    public static final String MCC = "mcc";

    public static final String MNC = "mnc";

    public static final String NUMERIC = "numeric";

    public static final String AUTH_TYPE = "authtype";

    public static final String TYPE = "type";

    public static final String CURRENT = "current";
}