var mysql = require('mysql');

var loggedin;
var username;

var con = mysql.createConnection({
    host: "cs-database.cs.loyola.edu",
    user: "jkitson",
    password: "1736438",
    database: "jajeimo",
    port: 3306
});

con.connect(function (err) {
    if (err) throw err;
    console.log("Connection to JAJEIMO Successful!");
});

exports.admin = function (res) {
    res.sendFile('admin.html', {root:'public'});
};


exports.auth = function (req, res) {
    console.log("USER " + req.body.username + " LOGGED IN!");
    username = req.body.username;
	let password = req.body.password;
	if (username && password) {
		con.query('SELECT * FROM accounts WHERE username = ? AND password = ?', [username, password], function(error, results, fields) {
			if (results.length > 0) {
				loggedin = true;
				res.redirect('/home');
			} else {
				res.send('Incorrect Username and/or Password!');
			}			
			res.end();
		});
	} else {
		res.send('Please enter Username and Password!');
		res.end();
	}
};


exports.home = function (req, res) {
    if (loggedin) {
        console.log('Welcome back, ' + username + '!');
        res.sendFile('home.html', {root:'public'});
    } else {
        res.send('Please login to view this page!');
        res.end();
    }
};

exports.animList = function (req, res) {
    res.setHeader('Content-Type', 'text/plain');
    con.query("SELECT * FROM Animals", function (err, result) {
        if (err) throw err;
        let json = JSON.parse(JSON.stringify(result));
        res.json(json);
    });
};