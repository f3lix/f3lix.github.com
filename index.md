---
layout: page
title: Hi Felix!
tagline: Stay Sharp
---
{% include JB/setup %}

<div class="posts">
  {% for post in site.posts %}
    <div><span>{{ post.date | date_to_string }}</span>&nbsp;<a href="{{ BASE_PATH }}{{ post.url }}"><h5>{{ post.title }}</h5></a></div>
  {% endfor %}
</div>

