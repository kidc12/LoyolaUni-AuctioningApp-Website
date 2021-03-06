var mysql = require('mysql');
var db_config = require('../db/dbaccess.js');

var loggedin;
var username;
var result = [];
const fs = require('fs');

var con = mysql.createConnection({
    host: db_config.host,
    user: db_config.user,
    password: db_config.password,
    database: db_config.database,
    port: db_config.port
});

con.connect(function (err) {
    if (err) {
        throw err;
    }
    console.log("Connection to JAJEIMO Successful!");
});

exports.admin = function (res) {
    res.sendFile('admin.html', {
        root: 'public'
    });
};


exports.auth = function (req, res) {
    console.log("USER " + req.body.username + " LOGGED IN!");
    username = req.body.username;
    let password = req.body.password;
    if (username && password) {
        con.query('SELECT * FROM admins WHERE username = ? AND password = ?', [username, password], function (error, results, fields) {
            if (results.length > 0) {
                loggedin = true;
                res.redirect('/home');
            } else {
                res.format({
                    'text/html': function () {
                        res.send('<center><h1>Incorrect Username and/or Password!</h1></center>')
                    }
                });
            }
            res.end();
        });
    } else {
        res.format({
            'text/html': function () {
                res.send('<center><h1>Please enter Username and Password!</h1></center>')
            }
        });
        res.end();
    }
};

exports.home = function (req, res) {
    if (loggedin) {
        console.log('Welcome back, ' + username + '!');
        con.query("SELECT * FROM items", function (err, result) {
            if (err) throw err;
            json = JSON.parse(JSON.stringify(result));
            for (let i = 0; i < json.length; i++) {
                var outputfile = "./views/img/" + json[i].id.toString() + ".png";
                var data = json[i].image.data;
                var buf = new Buffer(data, "binary");
                fs.writeFileSync(outputfile, buf);
            };

            con.query("SELECT * FROM admins", function (err, result) {
                if (err) throw err;
                json2 = JSON.parse(JSON.stringify(result));

                con.query("SELECT * FROM event", function (err, result) {
                    if (err) throw err;
                    
                    json3 = JSON.parse(JSON.stringify(result));

                    var outputfile = "./views/img/event.png";
                    var data = json3[0].image.data;
                    var buf = new Buffer(data, "binary");
                    
                    fs.writeFileSync(outputfile, buf);
                    
                    var currDate = new Date();
                    var endDate = json3[0].end.slice(0, json3[0].end.length - 13);
                    
                    endDate = endDate.split(".");
                    currDate = JSON.stringify(currDate);
                    currDate = currDate.slice(0, currDate.length - 15);
                    currDate = currDate.substr(1);
                    currDate = currDate.split("-");

                    console.log(endDate);
                    console.log(currDate);
                    
                    var done;
                    
                    if(endDate[0] === currDate[0] &&  endDate[1] === currDate[1] && parseInt(endDate[2]) >= parseInt(currDate[2])){
                        // console.log("true");
                        done == true;
                    }else{
                        // console.log("false");
                        done == false;
                    }

                    if (done) {
                        con.query("SELECT * FROM users WHERE FirstName IN ('Mollie' AND 'Jennifer')", function (err, result) {
                            if (err) throw err;
                            json4 = JSON.parse(JSON.stringify(result));
                            res.render('home', {
                                dataItem: json,
                                dataAdmin: json2,
                                dataEvent: json3,
                                dataWin: json4
                            });
                        });
                    } else {
                        res.render('home', {
                            dataItem: json,
                            dataAdmin: json2,
                            dataEvent: json3
                        });
                    }
                });
            });
        });
    } else {
        res.format({
            'text/html': function () {
                res.send('<center><h1>Please enter Username and Password!</h1></center>')
            }
        });
        res.end();
    }
};

exports.newpass = function (req, res) {
    res.send('new password');
};

function add() {
    if (err) throw err;
    var sql = "INSERT INTO customers (name, address) VALUES ('Company Inc', 'Highway 37')";
    con.query(sql, function (err, result) {
        if (err) throw err;
        console.log("1 record inserted");
    });
}


// function delete(table, column, info){
//     if (err) throw err;
//     var sql = "DELETE FROM " + table +" WHERE " + column + " = '" + info + "'";
//     con.query(sql, function (err, result) {
//       if (err) throw err;
//       console.log("Number of records deleted: " + result.affectedRows);
//     });
// }

function edit(table, column, newinfo, oldinfo) {
    if (err) throw err;
    var sql = "UPDATE " + table + " SET " + column + " = '" + newinfo + "' WHERE " + column + " = '" + oldinfo + "'";
    con.query(sql, function (err, result) {
        if (err) throw err;
        console.log(result.affectedRows + " record(s) updated");
    });
}