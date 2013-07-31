package components

import scala.swing.{SequentialContainer, Component}
import tools.JScrollablePanel

/**
 * author mikwie
 *
 */
class ScrollablePanel extends Component with SequentialContainer.Wrapper {

  override lazy val peer = new tools.JScrollablePanel()

  def scrollableWidth = peer.getScrollableWidth

  def scrollableWith_=(value: JScrollablePanel.ScrollableSizeHint) = peer.setScrollableWidth(value)

}
