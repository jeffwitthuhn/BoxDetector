#include <iostream>


using namespace std;

void swap(int &a, int &b){
	//cout<<a<<" "<<b<<endl;
	int temp=a;
	a=b;
	b=temp;
	//cout<<a<<" "<<b<<endl;

}
int main(){
	int *a;
	int *b;
	int c = 25;
	int d = 50;
	a = &c;
	b = &d;
	cout << a<<" "<<b<<endl;
	cout <<*a<<" "<<*b<<endl;

	int * temp;
	temp=a;
	a=b;
	b=temp;

	cout<<a<<" "<<b<<endl;
	cout<<*a<<" "<<*b<<endl;

	cout<<c<<" "<<d<<endl;
	swap(c,d);
	cout<<c<<" "<<d<<endl;

}