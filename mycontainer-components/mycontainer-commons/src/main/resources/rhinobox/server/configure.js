
Envjs.scriptTypes['application/javascript'] = true;
Envjs.scriptTypes['text/javascript'] = true;
Envjs.scriptTypes['text/xml'] = true;

var tmp = Envjs.connection;
Envjs.connection = function(xhr) {
	console.info("envjs connection: " + xhr.url);
	return Envjs.connection_.apply(this, arguments);
}
Envjs.connection_ = tmp;

Envjs.sync = function(fn){
    return fn;
};

Envjs.spawn = function(fn){
    return fn();
};
