package com.zmp.javaproject

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        val arr = intArrayOf(3, 8, 5, 1, 2, 6,9,3, 8, 5, 1, 2, 6,56,3, 8, 5, 1, 2, 6)
        quickSort(arr, 0, arr.size - 1)
        arr.forEach {
            print("$it  ")
        }
    }


    fun sort(arr: IntArray, start: Int, end: Int) {
        if (arr.size < 2 || start > end) {
            return
        }
        var left = start
        var right = end
        val mid = arr[left]
        while (left < right) {
            while (left < right) {
                if (arr[right] < mid) {
                    break
                }
                right--
            }
            if (left < right) {
                arr[left] = arr[right]
                left++
            }

            while (left < right) {
                if (arr[left] >= mid) {
                    break
                }
                left++
            }
            if (left < right) {
                arr[right] = arr[left]
                right--
            }
        }
        arr[left] = mid
        sort(arr, start, left - 1)
        sort(arr, left + 1, end)
    }

    fun sort2(arr: IntArray, start: Int, end: Int) {
        if (arr.size < 2 || start > end) {
            return
        }
        var left = start
        var right = end
        val mid = arr[left]
        while (left < right) {
            while (left < right && arr[right] > mid) {
                right--
            }
            if (left < right) {
                arr[left++] = arr[right]
            }

            while (left < right && arr[left] <= mid) {
                left++
            }
            if (left < right) {
                arr[right--] = arr[left]
            }
        }
        arr[left] = mid
        sort2(arr, start, left - 1)
        sort2(arr, left + 1, end)
    }

    fun quickSort(arr: IntArray, low: Int, high: Int) {
        var i: Int
        var j: Int
        val temp: Int
        var t: Int
        if (low > high) {
            return
        }
        i = low
        j = high
        //temp就是基准位
        val i1 = (low + high) shr 1
        temp = arr[i1]
        arr[i1] = arr[low]
        while (i < j) {
            //先看右边，依次往左递减
            while (temp <= arr[j] && i < j) {
                j--
            }
            //再看左边，依次往右递增
            while (temp >= arr[i] && i < j) {
                i++
            }
            //如果满足条件则交换
            if (i < j) {
                t = arr[j]
                arr[j] = arr[i]
                arr[i] = t
            }
        }
        //最后将基准为与i和j相等位置的数字交换
        arr[low] = arr[i]
        arr[i] = temp
        println("j:$j")
        println("i1:$i1")
        //递归调用左半数组
        quickSort(arr, low, j - 1)
//        //递归调用右半数组
        quickSort(arr, j + 1, high)
    }

}