package com.example.educards.lectures

data class SubjectFolder(
    val name: String,
    val lectureFiles: List<LectureFile>,
    var isExpanded: Boolean = false
)