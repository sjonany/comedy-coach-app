package com.comedy.suggester.chatparser

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import java.util.LinkedList
import java.util.Queue

/**
 * Helper methods for parsing chat
 */
private const val LOG_TAG = "ChatParserUtil"

/**
 * Level order traversal to find the first child w/ targetClass
 */
fun findFirstNodeWithClassName(
    rootInActiveWindow: AccessibilityNodeInfo,
    targetClass: String
): AccessibilityNodeInfo? {
    val queue: Queue<AccessibilityNodeInfo> = LinkedList()
    queue.add(rootInActiveWindow)

    while (queue.isNotEmpty()) {
        val currentNode = queue.poll()

        if (currentNode!!.className == targetClass) {
            return currentNode
        }

        for (i in 0 until currentNode.childCount) {
            val child = currentNode.getChild(i)
            if (child != null) {
                queue.add(child)
            }
        }
    }
    return null
}

/**
 * Find all leaf nodes with the max depth
 */
fun findMaxDepthLeaves(
    rootInActiveWindow: AccessibilityNodeInfo,
): List<AccessibilityNodeInfo> {
    var previousLayer: List<AccessibilityNodeInfo> = listOf()
    var currentLayer: Queue<AccessibilityNodeInfo> = LinkedList()
    var nextLayer: Queue<AccessibilityNodeInfo> = LinkedList()
    currentLayer.add(rootInActiveWindow)

    while (currentLayer.isNotEmpty()) {
        previousLayer = currentLayer.toList()
        while (currentLayer.isNotEmpty()) {
            val currentNode: AccessibilityNodeInfo = currentLayer.poll()!!
            for (i in 0 until currentNode.childCount) {
                val child = currentNode.getChild(i)
                if (child != null) {
                    nextLayer.add(child)
                }
            }
        }
        currentLayer = nextLayer
        nextLayer = LinkedList()
    }
    return previousLayer
}

/**
 * Helper function to debug UI tree.
 */
fun logNodeTree(node: AccessibilityNodeInfo?, depth: Int = 0) {
    if (node == null) return
    val prefix = " ".repeat(depth * 2)
    Log.d(
        LOG_TAG,
        "$prefix Class: ${node.className}, Text: ${node.text}, ContentDesc: ${node.contentDescription}"
    )
    for (i in 0 until node.childCount) {
        logNodeTree(node.getChild(i), depth + 1)
    }
}