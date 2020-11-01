package com.frikiplanet.proteo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentsAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private var fragments: MutableList<Fragment> = mutableListOf()

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun showFragments(fragments: List<Fragment>) {
        this.fragments = fragments.toMutableList()
        notifyDataSetChanged()
    }

    fun addFragmentAt(position: Int, fragment: Fragment) {
        fragments.add(fragment)
        notifyItemInserted(position)
    }

    fun removeFragmentAt(position: Int) {
        fragments.removeAt(position)
        notifyItemRemoved(position)
    }
}