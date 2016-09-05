package com.changhong.app.timeshift.common;

public class Class_Constant {

	// mainactivity handler type

	public final static int PLAY_BACKFROM_SHIFT = 2007;// voice down key
	public final static int KEYCODE_VOICE_DOWN = 25;// voice down key
	public final static int KEYCODE_VOICE_UP = 24;// voice up key
	public final static int KEYCODE_MUTE = 164;// mute key
	public final static int REPLAY_DIALOG_END_OK = 2008;
	public final static int REPLAY_DIALOG_END_CANCEL = 2009;
	public final static int TOAST_BANNER_PROGRAM_PASS = 3000;
	public final static int PLAY_LIVE = 3001;
	public static final int PLAY_URL = 3002;
	public static final int DIALOG_BANNER_PROGRAM_PASS = 3003;
	public static final int REPLAY_TIME_LENGTH = 3004;
	public static final int REPLAY_CHANNEL_DETAIL = 3005;
	public static final int REPLAY_SEEK_TO = 3006;
	public static final int RE_FAST_FORWARD_DOWN = 3007;
	public static final int RE_FAST_REVERSE_DOWN = 3008;
	public static final int RE_PLAY = 3009;
	public static final int RE_PAUSE = 3010;
	public static final int RE_UPDATE_PROGRESS = 3011;
	public static final int RE_NEXT_PROGRAM = 3012;
	public static final int RE_LAST_PROGRAM = 3013;
	public static final int LIVE_FAST_FORWARD = 3014;
	public static final int LIVE_FAST_REVERSE = 3015;
	public static final int MESSAGE_HANDLER_DIGITALKEY = 3016;
	public static final int MESSAGE_SHOW_DIGITALKEY = 3017;
	public static final int MESSAGE_DISAPPEAR_DIGITAL = 3018;
	public static final int RE_FAST_FORWARD_UP = 3019;
	public static final int RE_FAST_REVERSE_UP = 3020;
	public static final int BACK_TO_LIVE = 3021;
	public static final int DIALOG_ONKEY_DOWN = 3022;
	public static final int DIALOG_ONKEY_UP = 3023;
	public static final int DEFAULT_CHANNEL_NUMBER = 21;//boot default channel number
	public static final int LIVE_BACK_PROGRAM_OVER = 3024;//boot default channel number
	public static final int LIVE_FAST_FORWARD_UP = 3025;
	public static final int LIVE_FAST_REVERSE_UP = 3026;
	public static final int LIVE_BANNER_CURTIME = 3027;

	public final static int MAX_NO_OF_PROGRAMS = 500; // ����Ŀ��
	public final static int MAX_NO_OF_CARRIERS = 40; // ���Ƶ����
	public final static int MAX_NO_OF_TIMERS = 32; // ���ʱ����
	public final static int MAX_NO_OF_NVOD = 40; // ���NVOD��

	// PID
	public final static short PAT_PID = 0x00;
	public final static short CAT_PID = 0x01;
	public final static short NIT_PID = 0x10;
	public final static short SDT_PID = 0x11;
	public final static short EIT_PID = 0x12;
	public final static short RST_PID = 0x13;
	public final static short TDT_PID = 0x14;
	public final static short BAT_PID = 0x11;

	// Table_ID
	public final static short PAT_TABLE_ID = 0x00;
	public final static short CAT_TABLE_ID = 0x01;
	public final static short PMT_TABLE_ID = 0x02;
	public final static short NIT_ACTUAL_NETWORK_TABLE_ID = 0x40;
	public final static short NIT_OTHER_NETWORK_TABLE_ID = 0x41;
	public final static short SDT_ACTUAL_TS_TABLE_ID = 0x42;
	public final static short SDT_OTHER_TS_TABLE_ID = 0x46;
	public final static short BAT_TABLE_ID = 0x4a;
	public final static short EIT_TABLE_ID = 0x4e;
	public final static short cTDT_TABLE_ID = 0x70;
	public final static short TOT_TABLE_ID = 0x73;
	public final static short RUNNING_STATUS_TABLE_ID = 0x71;
	public final static short STUFFING_TABLE_ID = 0x72;
	public final static short CDT_TABLE_ID = 0x80;

	// DESC
	public final static short VIDEO_STREAM_DESC = 0x02;
	public final static short AUDIO_STREAM_DESC = 0x03;
	public final static short HIERARCHY_DESC = 0x04;
	public final static short REGISTRATION_DESC = 0x05;
	public final static short DATA_STREAM_ALIGNMENT_DESC = 0x06;
	public final static short TARGET_BKGND_GRID_DESC = 0x07;
	public final static short VIDEO_WINDOW_DESC = 0x08;
	public final static short CA_DESC = 0x09;
	public final static short ISO_639_LANGUAGE_DESC = 0x0A;
	public final static short SYSTEM_CLOCK_DESC = 0x0B;
	public final static short MULTIPLEX_BUFF_UTIL_DESC = 0x0C;
	public final static short COPYRIGHT_DESC = 0x0D;
	public final static short MAXIMUM_BITRATE_DESC = 0x0E;
	public final static short PRIVATE_DATA_INDICAT_DESC = 0x0F;
	public final static short NETWORK_NAME_DESC = 0x40;
	public final static short SERVICE_LIST_DESC = 0x41;
	public final static short LOGICAL_NUMBER_DES = 0x83;
	public final static short SERVICE_DESC = 0x48;
	public final static short SHORT_EVENT_DESC = 0x4d;
	public final static short AUDIO_COMPONENT_DESC = 0x50;
	public final static short VIDEO_COMPONENT_DESC = 0x51;
	public final static short CONTENT_DESC = 0x54;
	public final static short PARENTAL_RATING_DESC = 0x55;
	public final static short TELE_TEXT_DESC = 0x56;
	public final static short VCR_PROGRAMMING_DESC = 0x80;
	public final static short ACCESS_DESC = 0x81;
	public final static short CLOSED_CAPTION_DESC = 0x82;
	public final static short EIT_CONFIG_DESC = 0x83;
	public final static short SUBTITLE_DES = 0x59;

	// CRC����
	public final static int NO_OF_CRC_DATA_BYTES = 4;

	// PID����
	public final static short CHMID_PSI_CALLBACK_NUM = 48;

	// channel type��Ŀ���Ͷ���
	public final static short SERVICE_TYPE_RESERVE_0 = 0x00;
	public final static short SERVICE_TYPE_DIGITAL_TV = 0x01;
	public final static short SERVICE_TYPE_DIGITAL_RADIO = 0x02;
	public final static short SERVICE_TYPE_TELETEX = 0x03;
	public final static short SERVICE_TYPE_NVOD_REFERENCE = 0x04;
	public final static short SERVICE_TYPE_NVOD_TIME_SHIFT = 0x05;
	public final static short SERVICE_TYPE_MOSAIC = 0x06;
	public final static short SERVICE_TYPE_FM_RADIO = 0x07;
	public final static short SERVICE_TYPE_DVB_SRM = 0x08;
	public final static short SERVICE_TYPE_RESERVE_1 = 0x09;
	public final static short SERVICE_TYPE_ADVANCED_CODEC_RADIO = 0x0A;
	public final static short SERVICE_TYPE_ADVANCED_CODEC_MOSAIC = 0x0B;
	public final static short SERVICE_TYPE_DIGITAL_BROADCAST = 0x0C;
	public final static short SERVICE_TYPE_RESERVE_2 = 0x0D;
	public final static short SERVICE_TYPE_RCS_MAP = 0x0E;
	public final static short SERVICE_TYPE_RCS_FLS = 0x0F;
	public final static short SERVICE_TYPE_DVB_MHP = 0x10;
	public final static short SERVICE_TYPE_MPEG2_HD_DTV = 0x11;
	public final static short SERVICE_TYPE_RESERVE_3 = 0x12;
	public final static short SERVICE_TYPE_RESERVE_4 = 0x13;
	public final static short SERVICE_TYPE_RESERVE_5 = 0x14;
	public final static short SERVICE_TYPE_RESERVE_6 = 0x15;
	public final static short SERVICE_TYPE_ADVANCED_CODEC_SD_DTV = 0x16;
	public final static short SERVICE_TYPE_ADVANCED_CODEC_SD_NVOD_TIME_SHIFT = 0x17;
	public final static short SERVICE_TYPE_ADVANCED_CODEC_SD_NVOD_REFERENCE = 0x18;
	public final static short SERVICE_TYPE_ADVANCED_CODEC_HD_DTV = 0x19;
	public final static short SERVICE_TYPE_ADVANCED_CODEC_HD_NVOD_TIME_SHIFT = 0x1A;
	public final static short SERVICE_TYPE_ADVANCED_CODEC_HD_NVOD_REFERENCE = 0x1B;

	public final static int VKEY_EMPTY_DBASE = 0xe0000001;
	public final static int VKEY_DTV_BOOT_CHANNEL_CHECK = 0xe0000002;
	public final static int VKEY_DTV_BOOT_INIT_HW = 0xe0000003;

	public final static int VKEY_DTV_BOOT_REMOUTE_CTL = 0xe0000004;

	// KEYCODE����
	/*
	 * ����һ�����ң����
	 */

	public final static int KEYCODE_MENU_KEY = 82;
	public final static int KEYCODE_AUDIO_KEY = 1185;

	public final static int KEYCODE_UP_ARROW_KEY = 19;
	public final static int KEYCODE_DOWN_ARROW_KEY = 20;
	public final static int KEYCODE_LEFT_ARROW_KEY = 21;
	public final static int KEYCODE_RIGHT_ARROW_KEY = 22;

	public final static int KEYCODE_CHANNEL_UP = 166;
	public final static int KEYCODE_CHANNEL_DOWN = 167;

	public final static int KEYCODE_PAGE_UP = 92;
	public final static int KEYCODE_PAGE_DOWN = 93;

	public final static int KEYCODE_OK_KEY = 23;
	public final static int KEYCODE_INFO_KEY = 165;// 98;
	public final static int KEYCODE_BACK_KEY = 4;

	public final static int KEYCODE_RED_KEY = 131;// 131;//93;
	public final static int KEYCODE_GREEN_KEY = 132;// 132;
	public final static int KEYCODE_YELLOW_KEY = 133;// 94;
	public final static int KEYCODE_BLUE_KEY = 134;// 95;

	public final static int KEYCODE_KEY_DIGIT0 = 7;
	public final static int KEYCODE_KEY_DIGIT1 = 8;
	public final static int KEYCODE_KEY_DIGIT2 = 9;
	public final static int KEYCODE_KEY_DIGIT3 = 10;
	public final static int KEYCODE_KEY_DIGIT4 = 11;
	public final static int KEYCODE_KEY_DIGIT5 = 12;
	public final static int KEYCODE_KEY_DIGIT6 = 13;
	public final static int KEYCODE_KEY_DIGIT7 = 14;
	public final static int KEYCODE_KEY_DIGIT8 = 15;
	public final static int KEYCODE_KEY_DIGIT9 = 16;

	public final static int KEYCODE_KEY_DELETE = 67;
	public final static int TIME_LOOK_LIMIT = 300; // watch time more than this,
													// set it to FAV.
	public final static int TIME_RECORD_GAP = 20; // record the watch time every
													// 20 minutes.

	public final static int KEYCODE_KEY_PAUSE = 121;
	public final static int KEYCODE_KEY_FASTBACK = 89;

	// �˵�ID
	public final static int MENU_FINISH_DTV = -2;
	public final static int MENU_ID_DTV_ROOT = 0;
	public final static int MENU_ID_DTV_BANNER = 1;
	public final static int MENU_ID_DTV_CHANNEL_LIST = 2;
	public final static int MENU_ID_DTV_MAIN_MENU = 3;
	public final static int MENU_ID_DTV_SCAN_SET = 4;
	public final static int MENU_ID_DTV_SCAN = 5;
	public final static int MENU_ID_DTV_DIALOG = 6;
	public final static int MENU_ID_DTV_SETTING = 7;
	public final static int MENU_ID_DTV_CA_CONFIRM = 8;
	public final static int MENU_ID_DTV_CA_MAIN = 9;
	public final static int MENU_ID_DTV_CA_NORMAL = 10;
	public final static int MENU_ID_DTV_CA_NORMAL1 = 11;
	public final static int MENU_ID_DTV_FACTORY_RESET = 12;
	public final static int MENU_ID_DTV_USR_SET = 13;
	public final static int MENU_ID_DTV_VERSION = 14;
	public final static int MENU_ID_DTV_EPG_MENU = 15;

	// ������
	public final static int STREAM_TYPE_VIDEO_MPEG1 = 0x1;
	public final static int STREAM_TYPE_VIDEO_MPEG2 = 0x2;
	public final static int STREAM_TYPE_AUDIO_MPEG1 = 0x3;
	public final static int STREAM_TYPE_AUDIO_MPEG2 = 0x4;
	public final static int STREAM_TYPE_AUDIO_AC3 = 0x6;
	public final static int STREAM_TYPE_AUDIO_ADTS = 0xF;
	public final static int STREAM_TYPE_VIDEO_MPEG4 = 0x10;
	public final static int STREAM_TYPE_AUDIO_LATM = 0x11;
	public final static int STREAM_TYPE_VIDEO_H264 = 0x1B;
	public final static int STREAM_TYPE_AUDIO_DST_ALS_SLS = 0x1C;
	public final static int STREAM_TYPE_VIDEO_AVS = 0x42;
	public final static int STREAM_TYPE_AUDIO_AC3_A = 0x81;
	public final static int STREAM_TYPE_AUDIO_EAC3 = 0x87;
	public final static int STREAM_TYPE_VIDEO_VC1 = 0xEA;

	// ��Ƶ��ݱ����ʽ
	public final static int AUDIO_CODE_MPEG1 = 0;
	public final static int AUDIO_CODE_MPEG2 = 1;
	public final static int AUDIO_CODE_MP3 = 2;
	public final static int AUDIO_CODE_AC3 = 3;
	public final static int AUDIO_CODE_AAC_ADTS = 4;
	public final static int AUDIO_CODE_AAC_LOAS = 5;
	public final static int AUDIO_CODE_HEAAC_ADTS = 6;
	public final static int AUDIO_CODE_HEAAC_LOAS = 7;
	public final static int AUDIO_CODE_WMA = 8;
	public final static int AUDIO_CODE_AC3_PLUS = 9;
	public final static int AUDIO_CODE_LPCM = 10;
	public final static int AUDIO_CODE_DTS = 11;
	public final static int AUDIO_CODE_ATRAC = 12;
	public final static int AUDIO_CODE_MAX = 13;

	// ��Ƶ��ݱ����ʽ
	public final static int VIDEO_CODE_MPEG2 = 0;
	public final static int VIDEO_CODE_MPEG_HD = 1;
	public final static int VIDEO_CODE_MPEG4_ASP = 2;
	public final static int VIDEO_CODE_MPEG4_ASP_A = 3;
	public final static int VIDEO_CODE_MPEG4_ASP_B = 4;
	public final static int VIDEO_CODE_MPEG4_ASP_C = 5;
	public final static int VIDEO_CODE_DIVX = 6;
	public final static int VIDEO_CODE_VC1 = 7;
	public final static int VIDEO_CODE_H264 = 8;
	public final static int VIDEO_CODE_AVS = 9;
	public final static int VIDEO_CODE_MAX = 10;

	// ��Ƶ���
	public final static int AVM_SOUND_STERE0 = 0;/* ������ */
	public final static int AVM_SOUND_LEFT = 1;/* ����� */
	public final static int AVM_SOUND_RIGHT = 2;/* ����� */
	public final static int AVM_SOUND_MONO = 3;/* ����� */
	public final static int AVM_SOUND_AUTO = 4;/* �Զ� */
	public final static int AVM_SOUND_MAX = 5;/* ��Ч��� */

	// ��ǰ��Ŀ����
	public final static int SERVICE_TYPE_TV = 0;
	public final static int SERVICE_TYPE_RADIO = 1;

	// �Ի���
	public final static String DIALOG_TITLE = "DTV_DIALOG_TITLE";
	public final static String DIALOG_DETAILS = "DTV_DIALOG_DETAILS";
	public final static String DIALOG_BUTTON_NUM = "DTV_DIA_BUTTON_NUM";
	public final static String DIALOG_DEFAULT_BUTTON = "DTV_DIALOG_DEFAULT_BUTTON";
	public final static String DIALOG_TIME = "DTV_DIALOG_TIME";

	// ʱ��ƫ��
	public final static int TIME_ZONE_SHIFT = 0;

	// ��Ƶֹͣ--����������֡ģʽ
	public final static int VIDEO_STOP_MODE_BLACK = 0;
	public final static int VIDEO_STOP_MODE_IFRAME = 1;

	// CI �ص��¼�����
	public final static int CI_EVENT_NONE = 0;
	public final static int CI_EVENT_INIT_BEGIN = 1;
	public final static int CI_EVENT_INIT_SUCCESS = 2;
	public final static int CI_EVENT_INIT_FAILED = 3;
	public final static int CI_EVENT_DRAW_MENU = 4;
	public final static int CI_EVENT_DRAW_PINCODE = 5;
	public final static int CI_EVENT_EXIT = 6;
	public final static int CI_EVENT_CARD_OUT = 7;

	public final static int CI_QUERY_USER_MENU = 6;
	public final static int CI_QUERY_PINCODE = 7;
	public final static int CI_QUERY_INIT_MMI_DISPLAY = 8;
	public final static int CI_QUERY_CHANGE_CHANNEL = 10;

	public final static int CI_OPCODE_EXIT = 0;
	public final static int CI_OPCODE_OK = 1;
	public final static int CI_OPCODE_MENU = 2;

	// �������Ͷ���
	public final static int SEATCH_MODE_NIT = 1;
	public final static int SEATCH_MODE_LIST = 2;
	public final static int SEATCH_MODE_MANUAL = 3;

	public static final byte CHMID_TUNER_QAM_AUTO = 0;
	public static final byte CHMID_TUNER_QAM_QAM4 = 1;
	public static final byte CHMID_TUNER_QAM_QAM8 = 2;
	public static final byte CHMID_TUNER_QAM_QAM16 = 3;
	public static final byte CHMID_TUNER_QAM_QAM32 = 4;
	public static final byte CHMID_TUNER_QAM_QAM64 = 5;
	public static final byte CHMID_TUNER_QAM_QAM128 = 6;
	public static final byte CHMID_TUNER_QAM_QAM256 = 7;
	public static final byte CHMID_TUNER_QAM_QAM512 = 8;
	public static final byte CHMID_TUNER_QAM_QAM1024 = 9;

	public static final int TIMESHIFT_ERRORCODE_FAIL = 7000;
	public static final int TIMESHIFT_ERRORCODE_INVALID_APP = 7001;
	public static final int TIMESHIFT_ERRORCODE_INVALID_LIVE_CHAN = 7002;

}
