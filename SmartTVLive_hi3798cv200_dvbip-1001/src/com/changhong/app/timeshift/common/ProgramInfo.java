package com.changhong.app.timeshift.common;

import java.io.Serializable;
import java.util.Date;

/**
 * @author OscarChang
 *
 */
public class ProgramInfo implements Serializable{
	private int programId;
	private int channelID;
	private Date eventDate;
	private Date beginTime;
	private Date endTime;
	private String eventName;
	private String eventDesc;
	private String keyWord;
	private int isBook;
	private int isRecommend;
	private int playCount;
	private String assetID;
	private PosterInfo poster;
	private int playtime;
	private String volumeName;
	private int channelResourceCode;
	private String videoType;
	private String providerID;
	private String channelName;
	private int status;
	private String viewLevel;

	public int getProgramId() {
		return programId;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}

	public int getChannelID() {
		return channelID;
	}

	public void setChannelID(int channelID) {
		this.channelID = channelID;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public int getIsBook() {
		return isBook;
	}

	public void setIsBook(int isBook) {
		this.isBook = isBook;
	}

	public int getIsRecommend() {
		return isRecommend;
	}

	public void setIsRecommend(int isRecommend) {
		this.isRecommend = isRecommend;
	}

	public int getPlayCount() {
		return playCount;
	}

	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}

	public String getAssetID() {
		return assetID;
	}

	public void setAssetID(String assetID) {
		this.assetID = assetID;
	}

	public PosterInfo getPoster() {
		return poster;
	}

	public void setPoster(PosterInfo poster) {
		this.poster = poster;
	}

	public int getPlaytime() {
		return playtime;
	}

	public void setPlaytime(int playtime) {
		this.playtime = playtime;
	}

	public String getVolumeName() {
		return volumeName;
	}

	public void setVolumeName(String volumeName) {
		this.volumeName = volumeName;
	}

	public int getChannelResourceCode() {
		return channelResourceCode;
	}

	public void setChannelResourceCode(int channelResourceCode) {
		this.channelResourceCode = channelResourceCode;
	}

	public String getVideoType() {
		return videoType;
	}

	public void setVideoType(String videoType) {
		this.videoType = videoType;
	}

	public String getProviderID() {
		return providerID;
	}

	public void setProviderID(String providerID) {
		this.providerID = providerID;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getViewLevel() {
		return viewLevel;
	}

	public void setViewLevel(String viewLevel) {
		this.viewLevel = viewLevel;
	}

}
