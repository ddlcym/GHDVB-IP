/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.changhong.himp;
import android.os.Parcel;
import android.util.Log;
import android.os.ParcelFileDescriptor;

import android.media.MediaPlayer;
/**
 * HiMediaPlayer interface<br>
 *
 * HiMediaPlayer class is an extension of the MediaPlayer function interface,
 * not from the mediaplayer used alone.<br>
 * An example on how to use
 * this class can be found in com.hisilicon.android.videoplayer.activity.HisiVideoView.
 *
 * <p>Topics covered here are:
 * <ol>
 * <li><a href="#Attention">Attention</a>
 * <li><a href="#Hisi Mediaplayer Interfaces Description">Hisi Mediaplayer Interfaces Description</a>
  * <li><a href="#How to use HiMediaPlayer interface">How to use HiMediaPlayer interface</a>
 * </ol>
 *
 * <a name="Attention"></a>
 * <h3>Attention</h3>
 * <ul>
 * <li><b>Deprecated</b>:
 * indicate the section is useless</li>
 * <li>
 * <b>Currently not implemented:</b>indicate the section has not been implemented now,maybe it will be implemented in future.</li>
 * </ul>
 *
 * <a name="Hisi Mediaplayer Interfaces Description"></a>
 * <h3>Hisi Mediaplayer Interfaces Description</h3>
 *
 * <p>App must call android interface--MediaPlayer to construct
 * player,  to control playback of audio/video files and streams.
 * if you wan call hisi-extend interface--HiMediaPlayer,
 * you must construct a  HiMediaPlayer additional, and HiMediaPlayer relies on the Mediaplayer.
 * you can use HiMediaPlayerInvoke to get many hisi-extend useful function.We mainly describe HiMediaPlayer
 * here,you can visit android official web or read android comment of MediaPlayer to get more information
 * of MediaPlayer.
 * </p>
 *
 * <a name="How to use HiMediaPlayer interface"></a>
 * <h3>How to use HiMediaPlayer interface</h3>
 *
 * <li>
 * 1.First you should new MediaPlayer class object which include all operations playing a media file or
 * stream.eg."mMediaPlayer = new MediaPlayer();"</li>
 * <li>
 * 2.you should new HiMediaPlayer class object which include some hisi-extend
 * interface, and must input the used Mediaplayer instance to HiMediaPlayer. eg."mHiMediaPlayer = new HiMediaPlayer(mMediaPlayer);"</li>
 * <li>
 * 3.and then,you can use HiMediaPlayer interface. eg. switch subtitile
 function "mHiMediaPlayer.setSubTrack(pSubtitleId);"</li>
 */

public class HiMediaPlayer
{
    private final static String TAG = "HiMediaPlayer-Java";
    private final static String IMEDIA_PLAYER = "android.media.IMediaPlayer";
    private int timeOut = 12000;
    private MediaPlayer mediaplayer;

    static public class BufferConfig
    {
        public int start;
        public int enough;
        public int full;
        public int max;
    };

    /**
     * HiMediaPlayer initialization,you must input the player used MediaPlayer instance.<br>
     */
    public HiMediaPlayer(MediaPlayer media)
    {
        mediaplayer = media;
    }
    private int excuteCommand(int pCmdId, int pArg, boolean pIsGet)
    {
        Parcel Request = Parcel.obtain();
        Parcel Reply = Parcel.obtain();

        Request.writeInterfaceToken(IMEDIA_PLAYER);
        Request.writeInt(pCmdId);
        Request.writeInt(pArg);

        mediaplayer.invoke(Request, Reply);

        if (pIsGet)
        {
            Reply.readInt();
        }

        int Result = Reply.readInt();

        Request.recycle();
        Reply.recycle();

        return Result;
    }
    /**
     * Set the playback of the audio stream ID.
     * <br>
     * @param The int type, the index number of audio track info array you want to switch to<br>
     * @return Set the subtitle stream success. 0 - set successfully, -1 - set fail<br>
     */
    public int setAudioTrack(int track)
    {
        int flag = HiMediaPlayerInvoke.CMD_SET_AUDIO_TRACK_PID;

        return excuteCommand(flag, track, false);
    }
    /**
     * set audio channel mode.
     * <br>
     * @param channel Channel int types, values:<br>
     *                      0, stereo<br>
     *                      1, the left and right channel mixed output<br>
     *                      2, the left and right channel output left channel data<br>
     *                      3, the left and right channel output right channel data<br>
     *                      4, the left and right channel output, data exchange<br>
     *                      5, only the output of the right channel data<br>
     *                      6, only the output of left channel data<br>
     *                      7, mute<br>
     * @return Set the subtitle stream success. 0 - set successfully, -1 - set fail<br>
     */
    public int setAudioChannel(int channel)
    {
        int flag = HiMediaPlayerInvoke.CMD_SET_AUDIO_CHANNEL_MODE;

        return excuteCommand(flag, channel, false);
    }
    /**
     * Set caption display color.only for text subtitle,subtitle type is {@see HiMediaPlayerDefine.DEFINE_SUBT_FORMAT}
     * <br>
     * @param color Int type, value from 0x000000 - 0xFFFFFF high byte is R,middle byte is G,low byte is B<br>
     * @return Set the subtitle stream success. 0 - set successfully, -1 - set fail<br>
     */
    public int setSubFontColor(int color)
    {
        int flag = HiMediaPlayerInvoke.CMD_SET_SUB_FONT_COLOR;

        return excuteCommand(flag, color, false);
    }
    /**
     * Set caption display font type.only for text subtitle,subtitle type is {@see HiMediaPlayerDefine.DEFINE_SUBT_FORMAT}
     * <br>
     * @param style Style int types, values:<br>
     *              0, the normal<br>
     *              1, the shadow<br>
     *              2, hollow<br>
     *              3, the bold<br>
     *              4, italic<br>
     *              5, stroke<br>
     * @return Set the subtitle stream success. 0 - set successfully, -1 - set fail<br>
     */
    public int setSubFontStyle(int style)
    {
        int flag = HiMediaPlayerInvoke.CMD_SET_SUB_FONT_STYLE;

        return excuteCommand(flag, style, false);
    }
    /**
     * Set caption display font size.only for text subtitle,subtitle type is {@see HiMediaPlayerDefine.DEFINE_SUBT_FORMAT}
     * <br>
     * @param size Int type<br>
     * @return Set the subtitle stream success. 0 - set successfully, -1 - set fail<br>
     */
    public int setSubFontSize(int size)
    {
        int flag = HiMediaPlayerInvoke.CMD_SET_SUB_FONT_SIZE;

        return excuteCommand(flag, size, false);
    }

    /**
     * Set caption display character spacing.only for text subtitle,subtitle type is {@see HiMediaPlayerDefine.DEFINE_SUBT_FORMAT}
     * <br>
     * @param space Int type<br>
     * @return Set the subtitle stream success. 0 - set successfully, -1 - set fail<br>
     */
    public int setSubFontSpace(int space)
    {
        int flag = HiMediaPlayerInvoke.CMD_SET_SUB_FONT_SPACE;

        return excuteCommand(flag, space, false);
    }

    /**
     * Set caption display line spacing.only for text subtitle,subtitle type is {@see HiMediaPlayerDefine.DEFINE_SUBT_FORMAT}
     * <br>
     * @param linespace Int type<br>
     * @return Set the subtitle stream success. 0 - set successfully, -1 - set fail<br>
     */
    public int setSubFontLineSpace(int linespace)
    {
        int flag = HiMediaPlayerInvoke.CMD_SET_SUB_FONT_LINESPACE;

        return excuteCommand(flag, linespace, false);
    }
    /**
     * Set subtitle coding format.only for text subtitle,subtitle type is {@see HiMediaPlayerDefine.DEFINE_SUBT_FORMAT}
     * <br>
     * @param encode Int type<br>
              <table border="1" cellspacing="0" cellpadding="0">
              <tr>
                    <th>Value</th>
                    <th>Description</th>
              </tr>
              <tr>
                    <td>0</p></td>
                    <td>auto identify</p></td>
              </tr>
              <tr>
                  <td>1</p></td>
                  <td>Traditional Chinese(BIG5)</p></td>
             </tr>
             <tr>
                  <td>2</p></td>
                  <td>Universal Character Set(UTF8)</p></td>
             </tr>
             <tr>
                  <td>3</p></td>
                  <td>Western Europe(ISO8859_1)</p></td>
             </tr>
             <tr>
                  <td>4</p></td>
                  <td>Central Europe(ISO8859_2)</p></td>
             </tr>
             <tr>
                  <td>5</p></td>
                  <td>Southern Europe(ISO8859_3)</p></td>
             </tr>
             <tr>
                  <td>6</p></td>
                  <td>Nordic(ISO8859_4)</p></td>
             </tr>
             <tr>
                  <td>7</p></td>
                  <td>Slavic(ISO8859_5)</p></td>
             </tr>
                         <tr>
                  <td>8</p></td>
                  <td>Arabic(ISO8859_6)</p></td>
             </tr>
             <tr>
                  <td>9</p></td>
                  <td>Greek(ISO8859_7)</p></td>
             </tr>
             <tr>
                  <td>10</p></td>
                  <td>Hebrew(ISO8859_8)</p></td>
             </tr>
             <tr>
                  <td>11</p></td>
                  <td>Turkish(ISO8859_9)</p></td>
             </tr>
             <tr>
                  <td>12</p></td>
                  <td>Germanic(ISO8859_10)</p></td>
             </tr>
             <tr>
                  <td>13</p></td>
                  <td>Thai(ISO8859_11)</p></td>
             </tr>
             <tr>
                  <td>14</p></td>
                  <td>Baltic(ISO8859_13)</p></td>
             </tr>
             <tr>
                  <td>15</p></td>
                  <td>Celtic(ISO8859_14)</p></td>
             </tr>
             <tr>
                  <td>16</p></td>
                  <td>Western Europe(ISO8859_15)</p></td>
             </tr>
             <tr>
                  <td>17</p></td>
                  <td>Southeastern Europe(ISO8859_16)</p></td>
             </tr>
             <tr>
                  <td>18</p></td>
                  <td>Universal Character Set(UNICODE_16LE)</p></td>
             </tr>
             <tr>
                  <td>19</p></td>
                  <td>Universal Character Set(UNICODE_16BE)</p></td>
             </tr>
             <tr>
                  <td>20</p></td>
                  <td>Chinese(GBK)</p></td>
             </tr>
             <tr>
                  <td>21</p></td>
                  <td>Central Europe(CP1250)</p></td>
             </tr>
             <tr>
                  <td>22</p></td>
                  <td>Slavic(CP1251)</p></td>
             </tr>
             <tr>
                  <td>23</p></td>
                  <td>German(CP1252)</p></td>
             </tr>
             <tr>
                  <td>24</p></td>
                  <td>Greek(CP1253)</p></td>
             </tr>
             <tr>
                  <td>25</p></td>
                  <td>Turkish(CP1254)</p></td>
             </tr>
             <tr>
                  <td>26</p></td>
                  <td>Hebrew(CP1255)</p></td>
             </tr>
             <tr>
                  <td>27</p></td>
                  <td>Arabic(CP1256)</p></td>
             </tr>
             <tr>
                  <td>28</p></td>
                  <td>Baltic(CP1257)</p></td>
             </tr>
             <tr>
                  <td>29</p></td>
                  <td>Vietnamese(CP1258)</p></td>
             </tr>
             <tr>
                  <td>30</p></td>
                  <td>Thai(CP874)</p></td>
             </tr>
             <tr>
                  <td>31</p></td>
                  <td>Universal Character Set(UNICODE_32LE)</p></td>
             </tr>
             <tr>
                  <td>32</p></td>
                  <td>Universal Character Set(UNICODE_32BE)</p></td>
             </tr>
             </table>
     * @return Set the subtitle stream success. 0 - set successfully, -1 - set fail<br>
     */
    public int setSubEncode(int encode)
    {
        int flag = HiMediaPlayerInvoke.CMD_SET_SUB_FONT_ENCODE;

        return excuteCommand(flag, encode, false);
    }
    /**
     * Set caption display flow ID.
     * <br>
     * @param Int type,the subtitle index number(0 based) that you want to switch to<br>
     * @return Set the subtitle stream success. 0 - set successfully, -1 - set fail<br>
     */
    public int setSubTrack(int track)
    {
        int flag = HiMediaPlayerInvoke.CMD_SET_SUB_ID;

        return excuteCommand(flag, track, false);
    }
    /**
     * Set the caption and the synchronization time.
     * <br>
     * @param time Int type, used to adjust the caption display synchronization time, in MS unit.positive and negative number is valid<br>
     * @return Set the subtitle stream success. 0 - set successfully, -1 - set fail<br>
     */
    public int setSubTimeOffset(int time)
    {
        int flag = HiMediaPlayerInvoke.CMD_SET_SUB_TIME_SYNC;

        Parcel Request = Parcel.obtain();
        Parcel Reply = Parcel.obtain();

        Request.writeInterfaceToken(IMEDIA_PLAYER);
        Request.writeInt(flag);
        Request.writeInt(0);
        Request.writeInt(0);
        Request.writeInt(time);

        mediaplayer.invoke(Request, Reply);

        int Result = Reply.readInt();

        Request.recycle();
        Reply.recycle();

        return Result;
    }

        /**
     * Enable/Disable display subtitle.
     * <br>
     * @param int, <=0 -- enable. >0  -- disable<br>
     * @return 0 - set successfully, -1 - set fail<br>
     */
    public int enableSubtitle(int enable)
    {
        int flag = HiMediaPlayerInvoke.CMD_SET_SUB_DISABLE;

        return excuteCommand(flag, enable, false);
    }
    /**
     * Set caption display vertical position.
     * <br>
     * @param position Int type，subtitles distance from the bottom of the screen<br>
     * @return Set caption display vertical position of success. 0 - set successfully, -1 - set fail<br>
     */
    public int setSubVertical(int position)
    {
        int flag = HiMediaPlayerInvoke.CMD_SET_SUB_FONT_VERTICAL;

        return excuteCommand(flag, position, false);
    }
    /**
     * Import subtitle.
     * <br>
     * @param path,String type, subtitles path can be local file, also can be the network subtitles<br>
     * @return The success of import subtitle. 0 - into successful, -1 import failed<br>
     */
    public int setSubPath(String path)
    {
        int flag = HiMediaPlayerInvoke.CMD_SET_SUB_EXTRA_SUBNAME;
        Parcel Request = Parcel.obtain();
        Parcel Reply = Parcel.obtain();

        Request.writeInterfaceToken(IMEDIA_PLAYER);
        Request.writeInt(flag);
        Request.writeString(path);

        mediaplayer.invoke(Request, Reply);

        int Result = Reply.readInt();

        Request.recycle();
        Reply.recycle();

        return Result;
    }
    /**
     * Set playback speed.
     * <br>
     * @param speed Int type, range (-32, -16, -8, -4, -2,1,2,4,6,8,16,32).
     *              Less than 0 set to rewind playback, greater than 0 for fast playback, 1 indicates normal play.<br>
     * @return Speed play set success. 0 - set successfully, -1 - set fail<br>
     */
    public int setSpeed(int speed)
    {
        int flag;

        if(speed == 1)
        {
            flag = HiMediaPlayerInvoke.CMD_SET_STOP_FASTPLAY;
        }
        else if (speed == 2 || speed == 4 || speed == 8 || speed == 16 || speed == 32)
        {
            flag = HiMediaPlayerInvoke.CMD_SET_FORWORD;
        }
        else if (speed == -2 || speed == -4 || speed == -8 || speed == -16 || speed == -32)
        {
            flag  = HiMediaPlayerInvoke.CMD_SET_REWIND;
            speed = -speed;
        }
        else
        {
            Log.e(TAG,"setSpeed error:"+speed);
            return -1;
        }

        return excuteCommand(flag, speed, false);
    }
    /**
     * Gets the current audio and video format and file size, and metadata information.
     * <br>
     * @return To play the file information storage containers, including:
     * <table border="1" cellspacing="0" cellpadding="0">
     *   <tr>
     *     <th>Type</th>
     *     <th>Description</th>
     *   </tr>
     *   <tr>
     *     <td>int</p></td>
     *     <td>command execution results</p></td>
     *   </tr>
     *   <tr>
     *     <td>uint</p></td>
     *     <td>the current video coding format,<br>refer to {@see HiMediaPlayerDefine.DEFINE_VIDEO_ENCODING_FORMAT}</p></td>
     *   </tr>
     *   <tr>
     *     <td>uint</p></td>
     *     <td>audio coding format,<br>refer to {@see HiMediaPlayerDefine.DEFINE_AUDIO_ENCODING_FORMAT}</p></td>
     *   </tr>
     *   <tr>
     *     <td>long long</p></td>
     *     <td>file size</p></td>
     *   </tr>
     *   <tr>
     *     <td>int</p></td>
     *     <td>source type 0:local 1:vod 2:live</p></td>
     *   </tr>
     *     <td>String16</p></td>
     *     <td>Album</p></td>
     *   </tr>
     *   <tr>
     *     <td>String16</p></td>
     *     <td>Title</p></td>
     *   </tr>
     *   <tr>
     *     <td>String16</p></td>
     *     <td>Artist</p></td>
     *   </tr>
     *   <tr>
     *     <td>String16</p></td>
     *     <td>Genre</p></td>
     *   </tr>
     *   <tr>
     *     <td>String16</p></td>
     *     <td>Year</p></td>
     *   </tr>
     *   <tr>
     *     <td>String16</p></td>
     *     <td>date</p></td>
     *   </tr>
     * </table>
     */
    public Parcel getMediaInfo()
    {
        int flag = HiMediaPlayerInvoke.CMD_GET_FILE_INFO;
        Parcel Request = Parcel.obtain();
        Parcel Reply = Parcel.obtain();

        Request.writeInterfaceToken(IMEDIA_PLAYER);
        Request.writeInt(flag);

        mediaplayer.invoke(Request, Reply);

        Reply.setDataPosition(0);

        Request.recycle();

        return Reply;
    }

    private int setBufferMaxSizeConfig(int max)
    {
        int flag = HiMediaPlayerInvoke.CMD_SET_BUFFER_MAX_SIZE;
        Parcel Request = Parcel.obtain();
        Parcel Reply = Parcel.obtain();

        Request.writeInterfaceToken(IMEDIA_PLAYER);
        Request.writeInt(flag);
        Request.writeInt(max);

        mediaplayer.invoke(Request, Reply);

        int Result = Reply.readInt();

        Request.recycle();
        Reply.recycle();

        return Result;
    }

    private int getBufferMaxSizeConfig()
    {
        int flag = HiMediaPlayerInvoke.CMD_GET_BUFFER_MAX_SIZE;
        Parcel Request = Parcel.obtain();
        Parcel Reply = Parcel.obtain();

        Request.writeInterfaceToken(IMEDIA_PLAYER);
        Request.writeInt(flag);

        mediaplayer.invoke(Request, Reply);

        Reply.readInt();
        int Result = Reply.readInt();

        Request.recycle();
        Reply.recycle();

        return Result;
    }
    /**
     * Setting data buffer(local buffer in network stream playback) in buffer size threshold.
     * there is 4 value you can set.the 4 values are start,enough,full and max.<br>
     * <b>start:</b>if local buffer size reach start and is less than enough threshold you set,the {@link #MEDIA_INFO_BUFFER_START} message will be reported by
     * {@see com.hisilicon.android.mediaplayer.HiMediaPlayer.OnInfoListener} you set,{@link #MEDIA_INFO_BUFFER_START} means that
     * local buffer starts downloading,if you set {@link com.hisilicon.android.mediaplayer.HiMediaPlayerInvoke#CMD_SET_BUFFER_UNDERRUN},himediaplayer
     * will pause the playback in order to accumulate more buffer to enough state.because in start state,little media buffer data maybe
     * cause unsmooth playback.<br>
     * <b>enough:</b>when local buffer size reach enough and is less than full threshold you set, {@link #MEDIA_INFO_BUFFER_ENOUGH} message
     * will be reported,this message means local buffer is enough for playback,you can resume playback.if you set
     * {@link com.hisilicon.android.mediaplayer.HiMediaPlayerInvoke#CMD_SET_BUFFER_UNDERRUN},himediaplayer will resume the playback.<br>
     * <b>full:</b>now full is no use,but in some reason,you must set a value,the value must obey the principle:max > total > enough > start<br>
     * <b>max:</b>now useless.but in some reason,you must set a value,the value must obey the principle:max > total > enough > start<br>
     * <b>setting value must obey the principle:max > total > enough > start</b>
     * <br>
     * @param    BufferConfig: The BufferConfig type, the structure variables respectively:
     *           1,     start:int type, start the download, with the unit of KByte，
     *           2,     enough:int type, the buffered data to meet the playback requirements, can continue to play, to KByte as a unit.
     *           3,     full:int type, no use,but in some reason,you must set a value,the value must obey the principle:max > total > enough > start<br>
     *           4,     max:no use,but in some reason,you must set a value,the value must obey the principle:max > total > enough > start<br>
     * @return Data buffer size threshold setting success. 0 - set successfully, -1 - set fail<br>
     */
    public int setBufferSizeConfig(BufferConfig bufferConfig)
    {
        setBufferMaxSizeConfig(bufferConfig.max);

        int flag = HiMediaPlayerInvoke.CMD_SET_BUFFERSIZE_CONFIG;
        Parcel Request = Parcel.obtain();
        Parcel Reply = Parcel.obtain();

        Request.writeInterfaceToken(IMEDIA_PLAYER);
        Request.writeInt(flag);
        Request.writeInt(bufferConfig.full);
        Request.writeInt(bufferConfig.start);
        Request.writeInt(bufferConfig.enough);
        Request.writeInt(timeOut);

        mediaplayer.invoke(Request, Reply);

        int Result = Reply.readInt();

        Request.recycle();
        Reply.recycle();

        return Result;
    }
    /**
     * Set the size threshold in time value.
     * there is 4 value you can set.the 4 values are start,enough,full and max.<br>
     * <b>start:</b>if local buffer size reach start and is less than enough threshold you set,the {@link #MEDIA_INFO_BUFFER_START} message will be reported by
     * {@see com.hisilicon.android.mediaplayer.HiMediaPlayer.OnInfoListener} you set,{@link #MEDIA_INFO_BUFFER_START} means that
     * local buffer starts downloading,if you set {@link com.hisilicon.android.mediaplayer.HiMediaPlayerInvoke#CMD_SET_BUFFER_UNDERRUN},himediaplayer
     * will pause the playback in order to accumulate more buffer to enough state.because in start state,little media buffer data maybe
     * cause unsmooth playback.<br>
     * <b>enough:</b>when local buffer size reach enough and is less than full threshold you set, {@link #MEDIA_INFO_BUFFER_ENOUGH} message
     * will be reported,this message means local buffer is enough for playback,you can resume playback.if you set
     * {@link com.hisilicon.android.mediaplayer.HiMediaPlayerInvoke#CMD_SET_BUFFER_UNDERRUN},himediaplayer will resume the playback.<br>
     * <b>full:</b>no use,but in some reason,you must set a value,the value must obey the principle:max > total > enough > start<br>
     * <b>max:</b>the max buffer size in byte which bytes transformed from start,enough,full in time value can not exceed.<br>
     * <b>setting value must obey the principle:max > total > enough > start</b>
     * <br>
     * @param bufferConfig The BufferConfig type, the structure variables respectively:
     *           1,     start:int type, the current buffer data is not enough, can not meet the requirements to start the download, play, with the unit of MS
     *           2,     enough:int type, the buffered data to meet the playback requirements, can continue to play, to MS as a unit.
     *           3,     full:int type,no use,but in some reason,you must set a value,the value must obey the principle:max > total > enough > start<br>
     *           4,     max:int type, buffer only time setting is efficient in operation,
     *                      for some larger rate or resolution file, set the threshold size in byte which start,enough,full in time value can not exceed,
     *                      the max control memory usage threshold, always in KByte.<br>
     * @return Data buffer size threshold setting success. 0 - set successfully, -1 - set fail<br>
     */
    public int setBufferTimeConfig(BufferConfig bufferConfig)
    {
        setBufferMaxSizeConfig(bufferConfig.max);

        int flag = HiMediaPlayerInvoke.CMD_SET_BUFFERTIME_CONFIG;
        Parcel Request = Parcel.obtain();
        Parcel Reply = Parcel.obtain();

        Request.writeInterfaceToken(IMEDIA_PLAYER);
        Request.writeInt(flag);
        Request.writeInt(bufferConfig.full);
        Request.writeInt(bufferConfig.start);
        Request.writeInt(bufferConfig.enough);
        Request.writeInt(timeOut);

        mediaplayer.invoke(Request, Reply);

        int Result = Reply.readInt();

        Request.recycle();
        Reply.recycle();

        return Result;
    }
    /**
     * Gets the current playback buffer configuration size in byte.
     * <br>
     * @param bufferConfig The BufferConfig type, the structure variables respectively:<br>
     *          1,      start:int type, start the download, with the unit of KByte<br>
     *          2,      enough:int type, the buffered data to meet the playback requirements, can continue to play, to KByte as a unit.<br>
     *          3,      full:int type, the current buffer have all been used, data download full, with the unit of KByte<br>
     *          4,      max:int type, buffer only time setting is efficient in operation,<br>
     *                        for some rate or resolution of a larger file, set the threshold time will consume more memory space,
     *                        the max control memory usage threshold, always in KByte.<br>
     * @return Command execution results, 0 - to obtain information, -1 command failed<br>
     */
    public int getBufferSizeConfig(BufferConfig bufferConfig)
    {
        int flag = HiMediaPlayerInvoke.CMD_GET_BUFFERSIZE_CONFIG;
        Parcel Request = Parcel.obtain();
        Parcel Reply = Parcel.obtain();

        Request.writeInterfaceToken(IMEDIA_PLAYER);
        Request.writeInt(flag);

        mediaplayer.invoke(Request, Reply);

        Reply.readInt();

        bufferConfig.full   = Reply.readInt();
        bufferConfig.start  = Reply.readInt();
        bufferConfig.enough = Reply.readInt();
        bufferConfig.max = getBufferMaxSizeConfig();

        Request.recycle();
        Reply.recycle();

        return 0;
    }
    /**
     * Gets the current playback buffer configuration size in time(ms).
     * <br>
     * @param bufferConfig The BufferConfig type, the structure variables respectively:<br>
     *           1,     start:int type, the current buffer data is not enough, can not meet the requirements to start the download, play, with the unit of MS<br>
     *           2,     enough:int type, the buffered data to meet the playback requirements, can continue to play, to MS as a unit.<br>
     *           3,     full:int type, the current buffer have all been used, data download full, with the unit of MS<br>
     *           4,     max:int type, buffer only time setting is efficient in operation,
     *                      for some rate or resolution of a larger file, set the threshold time will consume more memory space,
     *                      the max control memory usage threshold, always in KByte.<br>
     * @return Command execution results, 0 - to obtain information, -1 command failed<br>
     */
    public int getBufferTimeConfig(BufferConfig bufferConfig)
    {
        int flag = HiMediaPlayerInvoke.CMD_GET_BUFFERTIME_CONFIG;
        Parcel Request = Parcel.obtain();
        Parcel Reply = Parcel.obtain();

        Request.writeInterfaceToken(IMEDIA_PLAYER);
        Request.writeInt(flag);

        mediaplayer.invoke(Request, Reply);

        Reply.readInt();

        bufferConfig.full   = Reply.readInt();
        bufferConfig.start  = Reply.readInt();
        bufferConfig.enough = Reply.readInt();
        bufferConfig.max = getBufferMaxSizeConfig();

        Request.recycle();
        Reply.recycle();

        return 0;
    }
    /**
     * The buffer size of the buffer data acquisition.
     * <br>
     * @return Returns the current buffer data size, with the unit of Kbytes.-1 call fail<br>
     */
    public int getBufferSizeStatus()
    {
        int flag = HiMediaPlayerInvoke.CMD_GET_BUFFER_STATUS;
        Parcel Request = Parcel.obtain();
        Parcel Reply = Parcel.obtain();

        Request.writeInterfaceToken(IMEDIA_PLAYER);
        Request.writeInt(flag);

        mediaplayer.invoke(Request, Reply);

        Reply.readInt();

        int Result = Reply.readInt();

        Request.recycle();
        Reply.recycle();

        return Result;
    }

    /**
     * Returns the current buffer data duration, with the unit of MS.
     * <br>
     * @return Returns the current cache data duration, with the unit of MS，-1 call fail<br>
     */
    public int getBufferTimeStatus()
    {
        int flag = HiMediaPlayerInvoke.CMD_GET_BUFFER_STATUS;
        Parcel Request = Parcel.obtain();
        Parcel Reply = Parcel.obtain();

        Request.writeInterfaceToken(IMEDIA_PLAYER);
        Request.writeInt(flag);

        mediaplayer.invoke(Request, Reply);

        Reply.readInt();
        Reply.readInt();

        int Result = Reply.readInt()/1000;

        Request.recycle();
        Reply.recycle();

        return Result;
    }

    /**
     * Gets the current network bandwidth information.
     * <br>
     * @return Bandwidth information storage containers, including:<br>
     *         1, int type, command execution results,The status code see system/core/include/utils/Errors.h<br>
     *         2, int type, the current bandwidth information(bps)<br>
     */
    public Parcel getNetworkInfo()
    {
        int flag = HiMediaPlayerInvoke.CMD_GET_DOWNLOAD_SPEED;
        Parcel Request = Parcel.obtain();
        Parcel Reply = Parcel.obtain();

        Request.writeInterfaceToken(IMEDIA_PLAYER);
        Request.writeInt(flag);

        mediaplayer.invoke(Request, Reply);

        Reply.setDataPosition(0);

        Request.recycle();

        return Reply;
    }
    /**
     * Currently not implemented
     * Set the current player frozen screen mode.
     * once you call {@link #reset()} --> {@link #setDataSource(FileDescriptor)} --> {@link #prepare()} to play next
     * file or stream.the last frozen picture int last file displays until next file plays if you set frozen mode.
     * black screen displays until next file plays if you set black screen mode.<br>
     * @param mode The int type, the last frame （0 - frozen screen）, （1 - black screen）<br>
     * @return Frozen screen mode is successful the player. 0 - set successfully, -1 - set fail<br>
     */
    public int setFreezeMode(int mode)
    {
        int flag = HiMediaPlayerInvoke.CMD_SET_VIDEO_FREEZE_MODE;

        return excuteCommand(flag, mode, false);
    }
    /**
     * Currently not implemented
     * Gets the current player frozen screen mode.
     * <br>
     * @return The current player frozen screen mode. The -1 command fails, the last frame 0 - frozen screen, 1 - frozen screen display screen<br>
     */
    public int getFreezeMode()
    {
        int flag = HiMediaPlayerInvoke.CMD_GET_VIDEO_FREEZE_MODE;
        Parcel Request = Parcel.obtain();
        Parcel Reply = Parcel.obtain();

        Request.writeInterfaceToken(IMEDIA_PLAYER);
        Request.writeInt(flag);

        mediaplayer.invoke(Request, Reply);

        Reply.readInt();

        int Result = Reply.readInt();

        Request.recycle();
        Reply.recycle();

        return Result;
    }
}
