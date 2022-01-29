package com.frikiplanet.proteo.model

class ItemHeader<Header: Comparable<Header>, Item> (
    val header: Header,
    val items: List<Item> = mutableListOf()
): Comparable<ItemHeader<Header, Item>> {

    override fun compareTo(other: ItemHeader<Header, Item>): Int = header.compareTo(other.header)
}