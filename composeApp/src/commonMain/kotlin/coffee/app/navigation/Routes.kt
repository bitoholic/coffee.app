object Routes {
    const val LIST = "list"
    const val DETAIL = "detail/{entryUuid}"
    const val FORM = "form?entryUuid={entryUuid}"
    const val SETTINGS = "settings"
    
    fun detail(entryUuid: String) = "detail/$entryUuid"
    fun form(entryUuid: String? = null) = if (entryUuid != null) "form?entryUuid=$entryUuid" else "form"
}