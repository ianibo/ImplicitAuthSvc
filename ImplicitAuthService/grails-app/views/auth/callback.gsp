<html>
<head>
</head>
  <body>

<script language="javascript">
  console.log("%s",window.location.href);
  console.log("%s",window.location.hash);
  console.log("%o",window.location);
  window.location='<g:createLink controller="auth" action="validateToken"/>?provider=${params.provider}&'+window.location.hash.substring(1);
</script>

  </body>
</html>
