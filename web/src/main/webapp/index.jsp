<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <script src="http://code.jquery.com/jquery-latest.min.js"></script>
    <title>Chat</title>
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
    <textarea rows="20" readonly id="chat"></textarea>
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

  <script>
    $(document).ready(function () {
      function ajax(){
        $.post(
          'ChatServlet', null,
          function (responseText) {
            $("#chat").append(responseText);

        });
      }
      setInterval(ajax, 1000);
    });

    $('#clientBtn').click(function () {
      console.log('log as client');
      var xhr = new XMLHttpRequest();
      xhr.open('POST', '/index', true);
      xhr.setRequestHeader('Content-Type', 'text/plain');
      var body = '/register client ' + $('#name').val();
      xhr.send(body);
    });

    $('#agentBtn').click(function () {
      console.log('log as agent');
      var xhr = new XMLHttpRequest();
      xhr.open('POST', '/index', true);
      xhr.setRequestHeader('Content-Type', 'text/plain');
      var body = '/register agent ' + $('#input').val();
      xhr.send(body);
    });

    $('#leaveBtn').click(function () {
      console.log('leave');
      var xhr = new XMLHttpRequest();
      xhr.open('POST', '/index', true);
      xhr.setRequestHeader('Content-Type', 'text/plain');
      var body = '/leave';
      xhr.send(body);
    });

    $('#sendBtn').click(function () {
      console.log('send');
      var xhr = new XMLHttpRequest();
      xhr.open('POST', '/index', true);
      xhr.setRequestHeader('Content-Type', 'text/plain');
      var body = $('#input').val();
      xhr.send(body);
    });

    $(document).on('onbeforeunload', function () {
      var xhr = new XMLHttpRequest();
      xhr.open('POST', '/index', true);
      xhr.setRequestHeader('Content-Type', 'text/plain');
      var body = '/exit';
      xhr.send(body);
    })
  </script>
  </body>
</html>
