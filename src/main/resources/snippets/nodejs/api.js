var express = require('express');
var session = require('express-session');
var cookieParser = require('cookie-parser');
var morgan = require('morgan');
var bodyParser = require('body-parser');
var methodOverride = require('method-override');

var mongoose = require('mongoose');

{{routers_requires}}

var app = express();

var server_port = process.env.PORT || 5000;
app.set('port', server_port);

var connection_string = process.env.DATABASE || 'mongodb://localhost/{{api_database}}';

mongoose.Promise = global.Promise;
mongoose.connect(connection_string);

var db = mongoose.connection;

db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
    console.log('Connection to MongoDB estabelished');
});

app.use(cookieParser())
app.use(session({
	secret: 'keyboard cat',
	resave: true,
	saveUninitialized: true
}));
app.use(morgan('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
	extended: true
}));
app.use(methodOverride());
{{routes}}
app.get("/",function(req, res) {
	res.send("Hello from {{APIName}}");
});
app.get("/api",function(req, res) {
	res.json({availableResources: availableResources()});
});

var baseURL= "http://localhost:" + app.get('port');
function availableResources() {
	var resourcesGet = [];
{{resources_get}}
	return resourcesGet;
}

app.listen(app.get('port'), function() {
	process.env.NODE_ENV = app.get('env');
	console.log("Listening on " + baseURL);
	var resources = availableResources();
	for (var r of resources)
		console.log(`${r.method} ${r.url}`);
})
