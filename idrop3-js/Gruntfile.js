/**
 * Created by Mike on 2/25/14.
 */
module.exports = function (grunt) {

    grunt.initConfig({
        pkg: grunt.file.readJSON("package.json"),

        // configured a task
        concat: {
            vendorjs: {
                src: ['js/vendor/**/*.js'],
                dest: 'web-app/js/vendor/vendor.js'
            },
            css: {
                src: ['css/*.css'],
                dest: 'web-app/css/app.css'
            }
        },

        /*
         distribute built javascript and web packages to the idrop3 main grails app
         TODO: add fonts, images
         https://www.npmjs.org/package/grunt-mcopy */
        copy: {
            main: {
                files: [
                    // copy assets to dist
                    {expand: true, src: ['assets/**/*.html'], dest: 'web-app'},
                    {expand: true, src: ['fonts'], dest: 'web-app'},
                    {expand: true, src: ['images'], dest: 'web-app'},
                    {expand: true, src: ['js/app/**/*.js'], dest: 'web-app'},
                    // includes files within path and its sub-directories
                    {expand: true, src: ['web-app/**'], dest: '../idrop-web3'}

                ]
            }
        },
        watch: {
            vendorjs: {
                files: ['<%= concat.vendorjs.src %>'],
                tasks: ['concat:vendorjs', 'copy']
            },
            js: {
                files: ['js/src/**/*.js'],
                tasks: ['copy']
            },
            css: {
                files: ['<%= concat.css.src %>'],
                tasks: ['concat:css', 'copy']
            },
            images: {
                files: ['images/*.*'],
                tasks: ['copy']
            },
            fonts: {
                files: ['fonts/*.*'],
                tasks: ['copy']
            },
            assets: {
                files: ['assets/**/*.html'],
                tasks: ['copy']
            }

        }
    });

    // loaded a task from npm module
    grunt.loadNpmTasks("grunt-contrib-concat");
    grunt.loadNpmTasks("grunt-contrib-watch");
    grunt.loadNpmTasks("grunt-mcopy");


    // loading of custom tasks
    grunt.loadTasks("tasks");

    // load a custom task

    // set our workflow
    grunt.registerTask("default", ["concat", "copy", "watch"]);

};