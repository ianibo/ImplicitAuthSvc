<html>
<head>
</head>
  <body>

<script language="javascript">
  console.log("Window Location Href: %s",window.location.href);
  console.log("Window Location Hash: %s",window.location.hash);
  console.log("Window Location : %o",window.location);
  console.log("Set window.location to point to validate token action, with appropriate provider");
  var validate_link = '<g:createLink controller="auth" action="validateToken"/>?provider=${params.provider}&'+window.location.hash.substring(1);
  console.log("validate link %s",validate_link);
  // window.location=validate_link;
</script>

  </body>
</html>
