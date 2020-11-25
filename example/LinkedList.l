/*
做了一个简单的链表，并演示了高阶函数的功能，比如在javascript中常用的map功能，
它能根据遍历列表中的每个元素，执行一个函数，并返回一个新的列表。给它传不同的函数，会返回不同的列表。
*/

// 链表的节点
class ListNode {
    int value;
    ListNode next;

    ListNode(int value) {
        this.value = value;
    }
}

// 链表
class LinkedList {
    ListNode start;
    ListNode end;

    // 添加新节点
    void add(int value) {
        ListNode newNode = ListNode(value);
        if (start == null) {
            start = newNode;
            end = newNode;
        } else {
            end.next = newNode;
            end = end.next;
        }
    }

    // 打印所有节点内容
    void dump() {
        ListNode head = start;
        while(head != null) {
            println(head.value);
            head = head.next;
        }
    }

    // 高阶函数功能，参数是一个函数，对每个成员做一个计算，形成一个新的LinkedList
    LinkedList map(function int(int) fun) {
        LinkedList newList = LinkedList();
        ListNode head = start;
        while (head != null) {
            int newValue = fun(head.value);
            newList.add(newValue);
            head = head.next;
        }
        return newList;
    }
}

//函数：平方值
int square(int value){
    return value * value;
}

//函数：加1
int addOne(int value){
    return value + 1;
}

int addTen(int value) {
    return value + 10;
}