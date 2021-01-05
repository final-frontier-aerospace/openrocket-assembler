package com.ffaero.openrocketassembler.model

import com.google.gson.annotations.SerializedName

class GitHubReleaseAsset {
	public var name: String? = null
	
	@SerializedName("browser_download_url")
	public var downloadURL: String? = null
	
	@SerializedName("content_type")
	public var contentType: String? = null
}
