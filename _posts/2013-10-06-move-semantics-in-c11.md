---
layout: post
title: "C++11中的Move Semantics"
description: "Move semantics in C++ 11"
category: tech
tags: [C++]
---
{% include JB/setup %}


### 1. Copy semantics

shallow copy和deep copy是C++中非常经典的问题。当用一个class A的变量初始化另一个同类型变量时，会调用class A的copy constructor。如果class A没有显示声明拷贝构造函数，那么编译器会隐式合成一个default copy constructor，并且展现出bitwise copy semantics，即shallow copy。

当然，如果class A中没有指针成员，这种拷贝是安全的。但是，如果class A中有指针成员，那么shallow copy会把指针成员的地址拷贝给另一个变量的指针成员，从而这两个变量的指针成员指向同一个地址。当这两个变量的其中一个被析构以后，另一个变量的指针成员就成了悬空的dangling pointer，从而析构的时候会出错。

于是我们知道，当一个class有指针成员的时候，通常要编写一个deep copy的拷贝构造函数，为指针成员分配新的内存。

考虑如下代码所示的场景。

{% gist 6849606 %}

class Giant有一个显示声明的copy constructor，并且是deep copy。在main函数中，CreateGiant()返回一个Giant的实例，用它来初始化giantA，并且用giantA初始化giantB。程序运行结果如下所示。

<pre>
Giant constructor
Giant copy constructor
Giant destructor
Giant copy constructor
Giant destructor
Giant @0x7fea704039d0
Giant copy constructor
Giant @0x7fea70403a00
Giant destructor
Giant destructor
</pre>

在这个程序中一共发生了三次拷贝。

1. CreateGiant()函数返回的时候调用一次构造函数，一次拷贝构造函数，拷贝到一个临时变量，然后调用返回值的析构函数。
2. 初始化giantA的时候调用一次拷贝构造函数，从临时变量拷贝到giantA，然后调用临时变量的析构函数。
3. 用giantA初始化giantB的时候调用一次拷贝构造函数。最后调用giantA和giantB的析构函数。

class Giant有一个指针成员list，如果list指向的内存很大，那么每次拷贝的时候都要重新分配内存，这样造成了性能上的负担。比如CreateGiant()返回的时候拷贝给临时变量，然后临时变量再拷贝给giantA，这两次拷贝其实意义不大。这时我们想能不能通过移动指针成员指向内存的ownership，来解决不必要拷贝的问题。

### 2. Rvalue reference

在C++98/03中，引用即另一个对象的别名。但仅限于引用一个lvalue，不可以引用一个rvalue，也就是说只有lvalue reference。

<pre>
int i = 42;
int& r = i; // lvalue reference
// int& var = 42; // won't compile
</pre>

当然这不完全正确，一个const reference既可以引用lvalue，也可以引用rvalue，比如一个接受const reference作为parameter的函数，会隐式地把argument转换成const reference。

<pre>
const int& r = 42; // const reference
</pre>

C++11中引入了一个新的引用类型，rvalue reference，仅限于引用一个rvalue，用两个&符号表示。

<pre>
int&& r = 42; // rvalue reference
int i = 42;
// int&& var = i; // cannot be bound to a lvalue
</pre>

在C++98/03中，一个值只可能是lvalue或者rvalue。

在C++11中，lvalue和之前一样，可以取地址、有名字。xvalue是expiring value，表示rvalue reference操作返回的值。prvalue是pure rvalue，和C++98/03中的rvalue意义相同。

C++11标准规定，所有值必须是lvalue，xvalue，prvalue三种之一。

### 3.Move semantics

根据我们之前的讨论，可以通过移动指针成员指向内存区域的ownership来减少临时变量带来的拷贝开销。C++11提供了一种新的构造函数，move constructor，接收一个rvalue reference作为参数，把待移动的对象的指针成员的值拷贝过来，同时把待移动对象的指针成员置空，相当于一次ownership的移动。不同于copy semantics，在move semantics中由于不涉及到内存分配，减少了一些不必要的开销。

下面使用move constructor重构第一小节的代码。

{% gist 6849612 %}

std::move()是标准库在utility中提供的函数，它可以把一个lvalue转换为rvalue reference，用于move semantics。实际上move()不会移动任何东西，如果转换失败还会调用copy constructor。程序运行结果如下所示。

<pre>
Giant constructor
Giant move constructor
Giant destructor
Giant move constructor
Giant destructor
Giant @0x7f8602c03970
Giant move constructor
Giant @0x7f8602c03970
Giant destructor
Giant destructor
</pre>

可以看出，经过move semantics，giantB的指针成员指向的地址与giantA的指针成员指向的地址相同。在这个过程中并没有调用copy constructor，减少了分配内存的开销。期间一共发生了三次移动。

1. CreateGiant()函数返回的时候调用一次构造函数，一次移动构造函数，移动到一个临时变量，然后调用返回值的析构函数。
2. 初始化giantA的时候调用一次移动构造函数，从临时变量移动到giantA，然后调用临时变量的析构函数。
3. 用giantA初始化giantB的时候，因为使用move()把giantA转换成了rvalue reference，调用一次移动构造函数。最后调用giantA和giantB的析构函数。注意这里如果使用我们之前讨论的default bitwise copy，两次析构会出错。

### 3. Misc

move semantics会修改rvalue，因此要避免把参数声明为const。

<pre>
Giant(const Giant&&); // cannot move from const argument
const Giant CreateGiant(); // const temp variable cannot be moved
</pre>

在move constructor中抛出异常也是危险的，因为可能造成dangling pointer。标准库提供了std::move_if_noexcept()函数，确保只有move constructor被声明为noexcept时才会转换成rvalue reference，否则转换成lvalue reference从而调用copy constructor。

<pre>
注：示例代码使用clang++编译通过。注意clang默认的标准库libstdc++是基于gcc4.2的，这样确保了linker可以链接gcc编译的代码和clang编译的代码，但是不支持C++11的一些新特性。通过指定标准库为libc++可以解决这个问题。同时为了观察拷贝构造函数的调用，请关闭编译器对临时变量的优化（RVO/NRVO）。
</pre>

<pre>
$ clang++ --version
Apple LLVM version 4.2 (clang-425.0.28) (based on LLVM 3.2svn)
Target: x86_64-apple-darwin12.5.0
Thread model: posix
$ clang++ -std=c++11 -stdlib=libc++ -fno-elide-constructors -o move move.cpp
$ ./move
</pre>