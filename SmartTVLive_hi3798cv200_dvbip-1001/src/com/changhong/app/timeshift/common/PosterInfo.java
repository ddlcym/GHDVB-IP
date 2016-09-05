package com.changhong.app.timeshift.common;

import java.sql.Date;

/**
 * @author OscarChang
 *
 */
public class PosterInfo {
	private int PosterID;
	private String OwnerCode;
	private int PosterType;
	private String FileName;
	private Date UploadTime;
	private int FileSize;
	private int Status;
	private String LocalPath;
	private int Width;
	private int Height;
	private String Platform;
	private String resolution;
	private String terminalType;
	private String assetFileCode;
	private String serverLocalPath;

	public int getPosterID() {
		return PosterID;
	}

	public void setPosterID(int posterID) {
		PosterID = posterID;
	}

	public String getOwnerCode() {
		return OwnerCode;
	}

	public void setOwnerCode(String ownerCode) {
		OwnerCode = ownerCode;
	}

	public int getPosterType() {
		return PosterType;
	}

	public void setPosterType(int posterType) {
		PosterType = posterType;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public Date getUploadTime() {
		return UploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		UploadTime = uploadTime;
	}

	public int getFileSize() {
		return FileSize;
	}

	public void setFileSize(int fileSize) {
		FileSize = fileSize;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public String getLocalPath() {
		return LocalPath;
	}

	public void setLocalPath(String localPath) {
		LocalPath = localPath;
	}

	public int getWidth() {
		return Width;
	}

	public void setWidth(int width) {
		Width = width;
	}

	public int getHeight() {
		return Height;
	}

	public void setHeight(int height) {
		Height = height;
	}

	public String getPlatform() {
		return Platform;
	}

	public void setPlatform(String platform) {
		Platform = platform;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	public String getAssetFileCode() {
		return assetFileCode;
	}

	public void setAssetFileCode(String assetFileCode) {
		this.assetFileCode = assetFileCode;
	}

	public String getServerLocalPath() {
		return serverLocalPath;
	}

	public void setServerLocalPath(String serverLocalPath) {
		this.serverLocalPath = serverLocalPath;
	}

}
