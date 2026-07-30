package coffee.app.domain

enum class SortOption(val displayName: String) {
    STARRED("Starred"),
    CreatedDateAsc("Date Added ↑"),
    CreatedDateDesc("Date Added ↓"),
    BeanNameAsc("Bean Name ↑"),
    BeanNameDesc("Bean Name ↓"),
    OriginAsc("Bean Origin ↑"),
    OriginDesc("Bean Origin ↓"),
    LastModifiedDateAsc("Date Modified ↑"),
    LastModifiedDateDesc("Date Modified ↓"),
}