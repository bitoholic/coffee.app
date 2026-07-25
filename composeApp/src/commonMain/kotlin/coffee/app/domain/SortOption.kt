package coffee.app.domain

enum class SortOption(val displayName: String) {
    CreatedDateAsc("Date ↑"),
    CreatedDateDesc("Date ↓"),
    BeanNameAsc("Name ↑"),
    BeanNameDesc("Name ↓"),
    OriginAsc("Origin ↑"),
    OriginDesc("Origin ↓"),
    LastModifiedDateAsc("Modified ↑"),
    LastModifiedDateDesc("Modified ↓")
}