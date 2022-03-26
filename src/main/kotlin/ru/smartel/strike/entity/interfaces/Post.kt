package ru.smartel.strike.entity.interfaces

import ru.smartel.strike.entity.PostEntity

interface Post : HavingTitles, HavingContents, Identifiable {
    var post: PostEntity

    override var titleRu: String?
        get() = post.titleRu
        set(value) {}
    override var titleEn: String?
        get() = post.titleEn
        set(value) {}
    override var titleEs: String?
        get() = post.titleEs
        set(value) {}
    override var titleDe: String?
        get() = post.titleDe
        set(value) {}
    override var contentRu: String?
        get() = post.contentRu
        set(value) {}
    override var contentEn: String?
        get() = post.contentEn
        set(value) {}
    override var contentEs: String?
        get() = post.contentEs
        set(value) {}
    override var contentDe: String?
        get() = post.contentDe
        set(value) {}
}