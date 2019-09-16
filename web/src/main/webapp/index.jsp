<%--
  Created by IntelliJ IDEA.
  User: Lenovo
  Date: 11.09.2019
  Time: 16:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head><script src="http://code.jquery.com/jquery-latest.min.js"></script>
    <title>Chat</title>
    <script>
      $(document).ready(function () {
        function ajax(){
          $.get('Chat', {id : ${ID}}, function(responseText) {
            $("#chat").append(responseText);
          });
        }

        setInterval(ajax, 1000);
      });

      $('#clientBtn').onclick(function () {
        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'web_war/${ID}', true);
        xhr.setRequestHeader('Content-Type', 'text/plain');
        var body = '/register client ' + $('#name').val();
        xhr.send(body);
      });

      $('#agentBtn').onclick(function () {
        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'web_war/${ID}', true);
        xhr.setRequestHeader('Content-Type', 'text/plain');
        var body = '/register agent ' + $('#input').val();
        xhr.send(body);
      });

      $('#leaveBtn').onclick(function () {
        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'web_war/${ID}', true);
        xhr.setRequestHeader('Content-Type', 'text/plain');
        var body = '/leave';
        xhr.send(body);
      });

      $('#sendBtn').onclick(function () {
        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'web_war/${ID}', true);
        xhr.setRequestHeader('Content-Type', 'text/plain');
        var body = $('#input').val();
        xhr.send(body);
      });

      $(document).on('onbeforeunload', function () {
        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'web_war/${ID}', true);
        xhr.setRequestHeader('Content-Type', 'text/plain');
        var body = '/exit';
        xhr.send(body);
      })
    </script>
  </head>
  <body>
  <div>
    Input your name:
  </div>
  <div>
    <input id="name"/>
  </div>
  <div>
    <button id="clientBtn">
      Login as client
    </button>
    <button id="agentBtn">
      Login as agent
    </button>
  </div>
  <div>
    Your chat:
  </div>
  <div>
    <textarea readonly id="chat"></textarea>
  </div>
  <div>
    <div>
      <input id="input"/>
    </div>
  </div>
  <div>
    <button id = 'sendBtn'>
      Send
    </button>
    <button id = 'leaveBtn'>
      Leave
    </button>
  </div>
  </body>
</html>
