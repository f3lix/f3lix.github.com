---
layout: post
title: "C++11中的类型推导"
description: "Type deduction in C++ 11"
category: tech
tags: [C++]
---
{% include JB/setup %}

### 1. auto

有时候你会听到一句话，“人生苦短，我用Python”。在C++里面，每个变量从生存周期开始的时候就会伴随一个具体的类型。但是对于Python这种动态类型的语言来说，就不是这样子了。

<pre>
var = "Hello "
print var + "World\n"
</pre>

很显然，上面这段Python会打印出Hello World，但是我们没有给变量var声明一个类型，这就是动态类型的好处。

首先C++为什么需要类型推导。在非泛型编程中，变量都有确定的类型，不需要类型推导。对于泛型编程，具体类型直到运行时才能确定，于是需要类型推导。RTTI为C++提供了运行时类型识别的机制，C++用typeid操作符和dynamic_cast两种方式实现了RTTI。typeid操作符返回一个type_info类型对象，通过name()方法可以获取类型名。另外在C++11中，为type_info添加了一个hash_code()方法，给类型比较带来了便利。

在C++里面，如果一个变量在编译期间没有类型，那么肯定会报错。于是有没有一个好的办法让C++在编译期间推导出变量类型呢。

在C++98/03标准里，auto关键字是一个storage class specifier，用来声明一个自动变量，表示空间自动分配。不过在大多数情况下，即使没有auto关键字，自动变量也会被隐式声明为auto。这样auto关键字其实很少会被用到，于是C++11标准重新定义了auto关键字。

<pre>
void foo()
{
	auto var = "Hello ";
	std::cout << var << "World\n";
}
</pre>

上面这段C++代码使用auto关键字自动推导了变量var的类型（char*），auto在C++11中被作为一个type specifier，编译期会在编译期间推导出变量类型。但是auto并不是一种类型，它只是一个place holder，编译期间会被变量的实际类型代替。

<pre>
void foo(std::vector<std::string>& vs)
{
	for(auto it = vs.begin(); it != vs.end(); ++it)
	{
		// do something
	}
}
</pre>

在这段代码中，函数foo接收一个string vector reference作为参数，在函数中遍历这个vector。在for循环中声明迭代器it的时候，不再需要一段冗长的std::vector<std::string>::iterator类型声明，取而代之的是一个auto。这样既方便书写，又增加了代码的可读性。

auto在一定程度上可以解决计算精度的问题。在不知道一个函数具体返回类型的时候，比如float或者double，可以使用auto最大限度保存计算精度。这样每次修改库的时候不需要重新编译客户端代码，即可做到自适应。但是auto无法解决计算中溢出的问题。

<pre>
double foo(float a)
{
	return (double) a * 3.1415926535897;
}
int main()
{
	float f = 1.414;
	auto var = foo(f);
}
</pre>

另外在宏定义的时候，因为无法知道变量类型，会有一些意想不到的效果。

<pre>
#define Max(a, b) ((a) > (b)) ? (a) : (b)
void foo()
{
	int max1 = Max(1, 2); // cool
	int max2 = Max(1 + 2, 3 * 4); // oops
}
</pre>

在Max的宏定义里面，其实a和b所代表的表达式会被计算两遍。当然你可能会想，把a和b的结果缓存下来再比较就可以了，这样你就需要知道a和b的类型。在C++98/03，类型推导不是一件容易的事情。但是在C++11中，有了auto以后，可以这样重构上面那段代码。

<pre>
#define Max(a, b) ({ auto _a = (a); auto _b = (b); (_a > _b) ? _a : _b; })
</pre>

当然，auto也有一些使用上的限制，以下列举出一部分。

1. auto不能做函数的形参类型，泛型参数还是需要用模板实现。
2. auto不能做模板实例化的参数。
3. 结构体中non static成员变量的类型不能是auto，编译器无法对结构体中非静态成员的类型进行推导。
4. 无法声明auto数组，编译器无法推导出数组的类型。
5. C++标准中，const和volatile组成了cv qualifier，auto无法带走表达式的cv限定符。比如把一个const int类型的变量赋给一个auto变量，那么这个变量的类型是int，而不是const int。

auto关键字在C++98/03里的含义在C++11标准中已被剥离，也就是说auto int i在C++98/03编译器中不会报错，但是在C++11中会报错。

### 2. decltype(expr)

C++11还提供了另一种类型推导decltype，与auto不同的是auto从变量的初始化表达式中获得变量类型，decltype以一个表达式作为参数，返回该表达式的类型。相同的是，auto和decltype都是在编译时期完成的。下面举几个例子。

<pre>
void foo()
{
	std::vector<std::string> vs;
	typedef decltype(vs.begin()) vsit;
	for(vsit it = vs.begin(); it != vs.end(); ++it)
	{
		// do something
	}
}
</pre>

<pre>
template<typename T1, typename T2>
decltype(a+b) sum(T1 a, T2 b)
{
	return a + b;
}

int main()
{
	int a = 42；
	double b = 1.414;
	auto var = sum(a, b); // var is double
}
</pre>

decltype也使得在不调用函数的情况下获取函数返回值类型成为了可能，标准库里面有一个基于decltype的模板类result_of。

<pre>
#include <type_traits>

double foo() { }

int main()
{
	result_of<foo()>::type var; // var is double
}
</pre>

当然decltype(expr)也有一些推导规则。

1. 如果表达式expr是一个没有括号的id expression（除去关键字、字面量等编译器所需标记外，程序员自定义的标记），那么返回值就是expr所表示实体的类型。
2. 如果expr是一个类型T的xvalue（expiring value，rvalue的一种），那么返回值是一个rvalue reference，T&&。
3. 如果expr是一个类型T的lvalue，那么返回值是一个lvalue reference，T&。
4. 如果expr的类型是T，那么返回值是类型T。
5. 如果expr是一个被重载的函数，编译器会报错。
6. 与auto不同，decltype可以带走表达式的cv限定符。

<pre>
void foo()
{
	int i;
	decltype(i) var1; // int
	decltype((i)) var2 = i; // int&
	decltype(i++) var3; // int
	decltype(++i) var4 = i; // int&
	decltype("Hello") var5 = "World"; // const char&

	const int ci = 42;
	decltype(ci) var6 = ci; // const int
}
</pre>

### 3. 追踪返回类型的函数声明

追踪返回类型的函数会把返回类型放在参数列表的后面。

<pre>
template<typename T1, typename T2>
auto sum(T1 a, T2 b) -> decltype(a+b)
{
	return a + b;
}

int main()
{
	int a = 42；
	double b = 1.414;
	auto var = sum(a, b); // var is double
}
</pre>

<pre>
int (*(*foo())()) ()
{
	return nullptr;
}
// it's gibberish!
// cdecl.org says: 
// declare foo as function returning pointer
// to function returning pointer
// to function returning int

auto bar() -> auto (*) ()  -> int (*) () ()
{
	return nullptr;
}
// it's readable!

int main()
{
	cout << is_same<decltype(foo), decltype(bar)>::value << endl;
}

// output: 1
</pre>

以上代码定义了两个完全相同的函数foo和bar，使用追踪返回类型的函数定义增加了代码的可读性。

### 4. 基于范围的for循环

通常在C++中，遍历一个数组需要知道数组的起始位置和长度，使用for，或者使用STL中的for_each来完成一个循环遍历。

<pre>
void print(int& n)
{
	cout << n << endl;
}

void foo()
{
	int arr[3] = { 1, 2, 3 };

	for(int* p = arr; p != arr + sizeof(arr) / sizeof(arr[0]); ++p)
	{
		cout << *p << endl;
	}

	for_each(arr, arr + sizeof(arr) / sizeof(arr[0]), print);
}
</pre>

C++11引入了类似Java的基于范围的for循环语法，配合类型推导，可以重构上面的代码。

<pre>
void foo()
{
	int arr[3] = { 1, 2, 3 };

	for(auto var : arr)
	{
		cout << var << endl;
	}
}
</pre>

当然，这种循环的前提是知道循环的范围。标准库中的容器一般不会有问题，对于数组而言，要知道第一个元素和最后一个元素之间的距离。如果无法确定数组的长度，则不能使用基于范围的for循环。

<pre>
void foo(int a[])
{
	for(auto var : a)
	{
		cout << var << endl;
	}
}

void bar()
{
	int arr[3] = { 1, 2, 3 };
	foo(arr); // 退化成int*，长度丢失，编译器报错
}
</pre>

另外注意在之前使用迭代器访问容器的时候，每次返回的是一个迭代器对象，解引用以后才会得到实际对象。基于范围的for循环返回的直接是实际对象，不需要解引用。