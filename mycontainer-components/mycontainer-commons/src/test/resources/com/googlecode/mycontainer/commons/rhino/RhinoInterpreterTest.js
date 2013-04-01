var obj = {
		name: 'myname',
		num: 3,
		inc: function(v) {
			if(!v) {
				v = 1;
			}
			this.num = this.num + v;
			return this.num;
		},
		callback: function(c) {
			return c.onEvent(this.num);
		}
};
