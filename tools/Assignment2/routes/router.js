module.exports = function (app) {
    var controller = require('../controllers/controller');

    app.get('/', controller.index);
    app.get('/animals', controller.animList);
    app.get('/animals/:id', controller.anim);
};