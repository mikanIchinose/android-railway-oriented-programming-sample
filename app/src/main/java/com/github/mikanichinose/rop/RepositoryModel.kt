package com.github.mikanichinose.rop

private const val USER_NAME = "mikanIchinose"

data class RepositoryModel(
    val id: Int,
    val name: String,
) {
    val ogpImageUrl:String
        get() = "https://opengraph.githubassets.com/1a/$USER_NAME/$name"
}