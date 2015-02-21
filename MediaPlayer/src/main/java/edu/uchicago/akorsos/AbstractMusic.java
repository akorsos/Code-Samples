
package edu.uchicago.akorsos;

import javafx.scene.Node;

 abstract class AbstractMusic {
  protected final Music music;
  protected final Node viewNode;

  public AbstractMusic(Music music) {
    this.music = music;
    this.viewNode = initView();
  }

  public Node getViewNode() {
    return viewNode;
  }

  protected abstract Node initView();
}