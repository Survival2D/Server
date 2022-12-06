use survival2d;
db.createUser(
  {
    user:"root",
    pwd:"123456",
    roles:[ { role: "readWrite", db: "survival2d" }]
  }
)
