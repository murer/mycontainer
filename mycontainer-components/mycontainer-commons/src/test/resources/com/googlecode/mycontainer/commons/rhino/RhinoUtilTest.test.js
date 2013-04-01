(function($) {
	var Person = $.createClass({
		initialize : function(name) {
			this.name = name;
		},
	});
	$.createToString(Person, 'Person', 'name');

	var p1 = new Person('p1');
	var p2 = new Person('p2');
	$.assertEquals("p1", p1.name);
	$.assertEquals("p2", p2.name);
	p1.name = 'x1';
	p2.name = 'x2';
	$.assertEquals("x1", p1.name);
	$.assertEquals("x2", p2.name);

	$.assertEquals("[Person name=x1]", p1.toString());
	$.assertEquals("[Person name=x2]", p2.toString());

});
