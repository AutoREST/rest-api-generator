var express = require('express');
var router = express.Router();
var {{model_name}} = require('../models/{{resource_name}}');

{{head_route}}

{{get_route}}

{{get_route_props}}

{{post_route}}

{{put_route}}

{{patch_route}}

{{delete_route}}

module.exports = router;
