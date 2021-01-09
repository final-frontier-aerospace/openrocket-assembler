package com.ffaero.openrocketassembler.model

import com.google.gson.annotations.SerializedName

class GitHubReleaseAsset {
	var name: String? = null
	
	@SerializedName("browser_download_url")
	var downloadURL: String? = null
	
	@SerializedName("content_type")
	var contentType: String? = null
}
