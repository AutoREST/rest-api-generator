var express = require('express');
var router = express.Router();
var /*#model_name#*/ = require('../models//*#resource_name#*/');

/*#get_route#*/

router.head('//*#resource_name#*//', function(req, res) {
    var query = {};
    if (req.query.hasOwnProperty("matriculaAluno"))
        query.matriculaAluno = req.query.matriculaAluno;
    if (req.query.hasOwnProperty("numTurma"))
        query.numTurma = req.query.numTurma;
    /*#model_name#*/.find(query, function(err, doc) {
        if (err) {
            res.status(400).send();
        } else {
            if (doc)
                res.status(200).send();
            else
                res.status(404).send();
        }
    });
});

router.post('//*#resource_name#*//', function(req, res) {
    if (req.body.hasOwnProperty("matriculaAluno") && req.body.hasOwnProperty("numTurma")) {
        var query = {
            matriculaAluno: req.body.matriculaAluno,
            numTurma: req.body.numTurma
        };
        /*#model_name#*/.findOneAndUpdate(
            query, {
                matriculaAluno: req.body.matriculaAluno,
                numTurma: req.body.numTurma,
                notaG1: req.body.notaG1,
                notaG2: req.body.notaG2,
                faltas: req.body.faltas
            }, {
                new: true,
                upsert: true
            },
            function(err, doc) {
                if (err)
                    res.status(400).send();
                else
                    res.status(201).send(doc.cleanObject());
            }
        );
    } else {
        res.status(400).send();
    }
});

router.put('//*#resource_name#*//', function(req, res) {
    if (req.query.hasOwnProperty("matriculaAluno") && req.query.hasOwnProperty("numTurma")) {
        var query = {
            matriculaAluno: req.query.matriculaAluno,
            numTurma: req.query.numTurma
        };
        /*#model_name#*/.findOneAndUpdate(
            query, {
                matriculaAluno: req.query.matriculaAluno,
                numTurma: req.query.numTurma,
                notaG1: req.body.notaG1,
                notaG2: req.body.notaG2,
                faltas: req.body.faltas
            }, {
                new: true,
                upsert: true
            },
            function(err, doc) {
                if (err)
                    res.status(400).send();
                else
                    res.status(201).send(doc.cleanObject());
            }
        );
    } else {
        res.status(400).send();
    }
});

router.patch('//*#resource_name#*//', function(req, res) {
    if (req.query.hasOwnProperty("matriculaAluno") && req.query.hasOwnProperty("numTurma")) {
        var query = {
            matriculaAluno: req.query.matriculaAluno,
            numTurma: req.query.numTurma
        };
        /*#model_name#*/.findOne(query, function(err, doc) {
            if (err) {
                res.status(400).send();
            } else {
                if (doc) {
                    if (req.body.hasOwnProperty("notaG1"))
                        doc.notaG1 = req.body.notaG1;
                    if (req.body.hasOwnProperty("notaG2"))
                        doc.notaG2 = req.body.notaG2;
                    if (req.body.hasOwnProperty("faltas"))
                        doc.faltas = req.body.faltas;
                    doc.save(function(err) {
                        if (err) {
                            res.status(400).send();
                        } else {
                            res.status(200).send(doc.cleanObject());
                        }
                    });
                } else {
                    res.status(404).send();
                }
            }
        });
    } else {
        res.status(400).send();
    }
});

router.delete('//*#resource_name#*//', function(req, res) {
    if (req.query.hasOwnProperty("matriculaAluno") && req.query.hasOwnProperty("numTurma")) {
        var query = {
            matriculaAluno: req.query.matriculaAluno,
            numTurma: req.query.numTurma
        };
        /*#model_name#*/.findOneAndRemove(query, {}, function(err, doc) {
            if (err)
                res.status(400).send();
            else
                res.status(204).send();
        });
    } else {
        res.status(400).send();
    }
});

module.exports = router;
