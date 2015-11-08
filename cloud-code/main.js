// Obtain Nanodegree information from Udacity public API and store within database
// Seperate jobs created to reduce data load for Parse.saveAll function
   
// Backend job to obtain course dataset
Parse.Cloud.job("obtainCourse", function(request, response) {
  var courseDataSet = new Array();
  function DeleteCourses(){
      var query = new Parse.Query("Courses");
      query.notEqualTo("course_id", "9999X99");
      query.limit(1000);
      // get data from query and call function with the results
      return query.find().then(function(results) {
        var courseObject = {}; 
        courseObject.courses = {};
        courseObject.courses.items = [];
        for (var i = 0; i < results.length; ++i) {
          results[i].destroy();
        }
      });
    };
  DeleteCourses().then(function(){
    Parse.Cloud.httpRequest({
      url: 'https://www.udacity.com/public-api/v0/courses?projection=internal'
      }).then(function(httpResponse){
    // Handle response
    var parsedJSON = JSON.parse(httpResponse.text);
    function replaceAll(find, replace, str) {
        return str.replace(new RegExp(find, 'g'), replace);
      };
      for (var i = parsedJSON.courses.length - 1; i >= 0; i--) {
        var object = parsedJSON.courses[i];
        var Courses = Parse.Object.extend("Courses");
        var course = new Courses();
        var course_url = 'https://www.udacity.com/courses/'+parsedJSON.courses[i].slug;
        var course_name = object.slug.split("--")[0];
        var course_title = replaceAll("-", " ", course_name);
        var course_id = object.slug.split("--")[1];
        var tracks = object.tracks;
        var starter = object.starter;
        var level = object.level;
        var timeFrame = object.expected_duration+' '+object.expected_duration_unit;
        var openForEnrollment = object.open_for_enrollment;
        var summary = object.summary;
        var banner_image = object.banner_image;
        var subtitle = object.subtitle;
        var image = object.image;
        var available = object.available;
        var expected_learning = object.expected_learning;
        var featured = object.featured;
        var full_course_available = object.full_course_available;
        var new_release = object.new_release;
        var required_knowledge = object.required_knowledge;
        var short_summary = object.short_summary;
        var syllabus = object.syllabus;
        var tags = object.tags;
        course.set("course_title", course_title);
        course.set("course_id", course_id);
        course.set("course_url", course_url);
        course.set("summary", summary);
        course.set("tracks", tracks);
        course.set("starter", starter);
        course.set("level", level);
        course.set("time_frame", timeFrame);
        course.set("open_for_enrollment", openForEnrollment);
        course.set("banner_image", banner_image);
        course.set("subtitle", subtitle);
        course.set("image", image);
        course.set("available", available);
        course.set("expected_learning", expected_learning);
        course.set("featured", featured);
        course.set("full_course_available", full_course_available);
        course.set("new_release", new_release);
        course.set("required_knowledge", required_knowledge);
        course.set("short_summary", short_summary);
        course.set("syllabus", syllabus);
        course.set("tags", tags);
        courseDataSet[i] = course;
      }
      console.log("Total courses obtained = "+courseDataSet.length);   
      Parse.Object.saveAll(courseDataSet, {
        success: function(list){
          // all objects saved to database
          response.success('Courses updated');
        },
        error: function(list){
          response.error('Error saving list data');
        }
      });
      console.log('Udacity API request completed');
    }, function(error){
      return Parse.Promise.error("Yikes, we have a parse.promise error: "+error.message);
    })
  })
});
   
// Backend Job to obtain Nanodegree dataset
Parse.Cloud.job("obtainNanodegrees", function(request, response) {
  var degreeList = new Array();
  function DeleteNanodgrees(){
      var query = new Parse.Query("Nanodegree");
      query.notEqualTo("degree_id", "9999X99");
      query.limit(1000);
      // get data from query and call function with the results
      return query.find().then(function(results) {
        var degreeObject = {}; 
        degreeObject.degrees = {};
        degreeObject.degrees.items = [];
        for (var i = 0; i < results.length; ++i) {
          results[i].destroy();
        }
      });
    };
  DeleteNanodgrees().then(function(){
    Parse.Cloud.httpRequest({
      url: 'https://www.udacity.com/public-api/v0/courses?projection=internal'
      }).then(function(httpResponse){
    // Handle response
    var parsedJSON = JSON.parse(httpResponse.text);
    function replaceAll(find, replace, str) {
        return str.replace(new RegExp(find, 'g'), replace);
      };
    for (var i = parsedJSON.degrees.length - 1; i >= 0; i--) {
        var object = parsedJSON.degrees[i];
        var Nanodegree = Parse.Object.extend("Nanodegree");
        var nanodegree = new Nanodegree();
        var degree_url = 'https://www.udacity.com/course/'+parsedJSON.degrees[i].slug;
        var degree_name = object.slug.split("--")[0];
        var degree_title = replaceAll("-", " ", degree_name);
        var degree_id = object.slug.split("--")[1];
        var banner_image = object.banner_image;
        var subtitle = object.subtitle;
        var image = object.image;
        var available = object.available;
        var expected_learning = object.expected_learning;
        var featured = object.featured;
        var full_course_available = object.full_course_available;
        var new_release = object.new_release;
        var required_knowledge = object.required_knowledge;
        var short_summary = object.short_summary;
        var syllabus = object.syllabus;
        var tags = object.tags;
        var tracks = object.tracks;
        var starter = object.starter;
        var level = object.level;
        var timeFrame = object.expected_duration+' '+object.expected_duration_unit;
        var openForEnrollment = object.open_for_enrollment;
        var summary = object.summary;
        nanodegree.set("degree_title", degree_title);
        nanodegree.set("degree_id", degree_id);
        nanodegree.set("degree_url", degree_url);
        nanodegree.set("summary", summary);
        nanodegree.set("tracks", tracks);
        nanodegree.set("starter", starter);
        nanodegree.set("level", level);
        nanodegree.set("time_frame", timeFrame);
        nanodegree.set("open_for_enrollment", openForEnrollment);
        nanodegree.set("banner_image", banner_image);
        nanodegree.set("subtitle", subtitle);
        nanodegree.set("image", image);
        nanodegree.set("available", available);
        nanodegree.set("expected_learning", expected_learning);
        nanodegree.set("featured", featured);
        nanodegree.set("full_course_available", full_course_available);
        nanodegree.set("new_release", new_release);
        nanodegree.set("required_knowledge", required_knowledge);
        nanodegree.set("short_summary", short_summary);
        nanodegree.set("syllabus", syllabus);
        nanodegree.set("tags", tags);
        degreeList[i] = nanodegree;
      }
      console.log("Total nanodegrees obtained = "+degreeList.length);   
      Parse.Object.saveAll(degreeList, {
        success: function(list){
          // all objects saved to database
          response.success('Nanodegrees updated');
        },
        error: function(list){
          response.error('Error saving list data');
        }
      });
      console.log('Udacity API request completed');
    }, function(error){
      return Parse.Promise.error("Yikes, we have a parse.promise error: "+error.message);
    })
  })
});
   
   
// This function will send a push notification to mobile clients if new articles 
// have been added to the datastore since the last notification.
   
Parse.Cloud.define("pushNotifyIfNewArticlesAdded", function(request, response) {
  // Cloud code does not maintain a global state across requests, so a Parse class
  // must be created to keep track of the last push notification date. 
  var lastPushQuery = new Parse.Query("LastPush");
  lastPushQuery.first({
    success: function(lastPush) {
      var lastPushAt = lastPush.get("lastPushAt");
      var articlesQuery = new Parse.Query("Articles");
      articlesQuery.descending("createdAt");
      articlesQuery.greaterThan("createdAt", lastPushAt);
      //articlesQuery.equalTo("approved", true);
      articlesQuery.first({
        success: function(article) {
          // Check whether there is an approved article with a created date greater
          // than the last push notification date.
          if (article != null && (article.get("createdAt") > lastPushAt)) {
            // push to all installations
            var pushQuery = new Parse.Query(Parse.Installation);
            Parse.Push.send({
              where: pushQuery,
              data: {
                alert: "newArticlesAdded"
              }
            }, {
              success: function() { 
                response.success("Push was successful");
                lastPush.set("lastPushAt", new Date());
                lastPush.save();
              },
              error: function(error) {
                response.error("An error occurred when pushing: " + error);
              } 
            });
          }
          else {
            response.success("No new articles to push");
          }
        },
        error: function() {
          response.error("Could not retrieve article");
        }
      });
    },
    error: function() {
      resonse.error("Could not retrieve last push");
    }
  });
});
    
// This job will execute the #pushNotifyIfNewArticlesAdded function.
Parse.Cloud.job("pushNotifyIfNewArticlesAddedJob", function(request, status) {
  Parse.Cloud.run("pushNotifyIfNewArticlesAdded", {
    success: function() {
      status.success();
    },
    error: function(error) {
      status.error(error);
    }
  });
});